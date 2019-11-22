package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Address;
import com.bankApi.bankApi.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomerService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerService(JdbcTemplate jdbcTemplate) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate must not be null.");
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsById(Long id) {
        List<Customer> c = jdbcTemplate.query("SELECT * FROM Customer WHERE CustomerId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Customer.class)));
        return !c.isEmpty();
    }

    public Customer findById(Long id) {
        List<Customer> c = jdbcTemplate.query("SELECT * FROM Customer WHERE CustomerId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Customer.class)));
        return c.get(0);
    }

    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT * FROM Customer", (new BeanPropertyRowMapper<>(Customer.class)));
    }

    @Transactional(rollbackFor = Exception.class)
    public Customer save(Customer customer){

        jdbcTemplate.update("INSERT INTO customer (first_name, last_name, email, password) VALUES (?, ?, ?, ?)",
                customer.getFirst_name(), customer.getLast_name(), customer.getEmail(), customer.getPassword());

        // Get the ID of the customer we just saved
        // just search by email because it's unique
        Long customerId = jdbcTemplate.queryForObject("SELECT CustomerId FROM customer WHERE email = ?", new Object[]{customer.getEmail()}, Long.class);

        // Do this for each of their addresses
        List<Long> addressList = new ArrayList<>();
        customer.getAddress().forEach(v -> {
            // Put it into the address table
            jdbcTemplate.update("INSERT INTO address (street_number, street_name, city, state, zip) VALUES (?, ?, ?, ?, ?)",
                    v.getStreet_number(), v.getStreet_name(), v.getCity(), v.getState(), v.getZip());

            // Now get the ID of the address we just inserted and add it to the arraylist of address IDs
            Long addressId = jdbcTemplate.queryForObject("SELECT AddressId from Address WHERE street_number = ? AND street_name = ? AND city = ? AND state = ? AND zip = ?",
                    new Object[] {v.getStreet_number(), v.getStreet_name(), v.getCity(), v.getState(), v.getZip()}, Long.class);
            addressList.add(addressId);

            v.setId(addressId);
        });

        //Now put the connection to their addresses in the join table
        addressList.forEach(v -> {
            jdbcTemplate.update("INSERT INTO customer_address (CustomerId, AddressId) VALUES (?, ?)",
                    customerId, v);
        });

        // make sure we return ID values with our response entity
        customer.setCustomerId(customerId);
        return customer;
    }
}
