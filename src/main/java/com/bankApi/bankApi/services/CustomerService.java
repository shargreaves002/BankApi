package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Address;
import com.bankApi.bankApi.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
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
        Customer c = jdbcTemplate.query("SELECT * FROM Customer WHERE CustomerId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Customer.class))).get(0);

        //get their address
        Address addressToReturn = jdbcTemplate.query("SELECT * FROM Address WHERE AddressId = (SELECT AddressId FROM Customer WHERE CustomerId = ?)", new Object[] {id}, new BeanPropertyRowMapper<>(Address.class)).get(0);
        // get the address ID
        addressToReturn.setId(jdbcTemplate.queryForObject("SELECT AddressId FROM Address WHERE street_number = ? AND street_name = ? AND city = ? AND state = ? AND zip = ?",
                new Object[]{addressToReturn.getStreet_number(), addressToReturn.getStreet_name(), addressToReturn.getCity(), addressToReturn.getState(), addressToReturn.getZip()}, Long.class));
        // set their address
        c.setAddress(addressToReturn);
        return c;
    }

    public List<Customer> findAll() {
        List<Long> customerIds =  jdbcTemplate.queryForList("SELECT CustomerId FROM Customer", Long.class);
        List<Customer> customers = new ArrayList<>();
        customerIds.forEach(v -> customers.add(this.findById(v)));
        return customers;
    }

    public Customer save(Customer customer){

        // Get their address
        Address v = customer.getAddress();
        //check to see if it's already in the table
        List<Address> address = jdbcTemplate.query("SELECT * from Address WHERE street_number = ? AND street_name = ? AND city = ? AND state = ? AND zip = ?",
                new Object[] {v.getStreet_number(), v.getStreet_name(), v.getCity(), v.getState(), v.getZip()}, new BeanPropertyRowMapper<>(Address.class));
        if (address.isEmpty()){
            // If not, put it into the address table
            jdbcTemplate.update("INSERT INTO address (street_number, street_name, city, state, zip) VALUES (?, ?, ?, ?, ?)",
                    v.getStreet_number(), v.getStreet_name(), v.getCity(), v.getState(), v.getZip());
        }

        // Now get the ID of the address we just inserted
        Long addressId = jdbcTemplate.queryForObject("SELECT AddressId from Address WHERE street_number = ? AND street_name = ? AND city = ? AND state = ? AND zip = ?",
                new Object[] {v.getStreet_number(), v.getStreet_name(), v.getCity(), v.getState(), v.getZip()}, Long.class);

        v.setId(addressId);

        jdbcTemplate.update("INSERT INTO customer (first_name, last_name, email, password, AddressId) VALUES (?, ?, ?, ?, ?)",
                customer.getFirst_name(), customer.getLast_name(), customer.getEmail(), customer.getPassword(), addressId);

        // Get the ID of the customer we just saved
        // just search by email because it's unique
        Long customerId = jdbcTemplate.queryForObject("SELECT CustomerId FROM customer WHERE email = ?", new Object[]{customer.getEmail()}, Long.class);

        // make sure we return ID values with our response entity
        customer.setCustomerId(customerId);
        return customer;
    }

    public Customer updateCustomer(Customer customer, long id) {
        //update customer details
        if (customer.getFirst_name() != null) {
            jdbcTemplate.update("UPDATE Customer SET first_name = ? WHERE CustomerId = ?", customer.getFirst_name(), id);
        }
        if (customer.getLast_name() != null) {
            jdbcTemplate.update("UPDATE Customer SET last_name = ? WHERE CustomerId = ?", customer.getLast_name(), id);
        }
        if (customer.getEmail() != null) {
            jdbcTemplate.update("UPDATE Customer SET email = ? WHERE CustomerId = ?", customer.getEmail(), id);
        }
        if (customer.getPassword() != null) {
            jdbcTemplate.update("UPDATE Customer SET password = ? WHERE CustomerId = ?", customer.getPassword(), id);
        }
        Address address = customer.getAddress();
        //update their address
        if (address != null) {
            if (address.getStreet_number() != null) {
                jdbcTemplate.update("UPDATE address SET street_number = ? WHERE AddressId = (SELECT AddressId FROM customer WHERE CustomerId = ?)", address.getStreet_number(), id);
            }
            if (address.getStreet_name() != null) {
                jdbcTemplate.update("UPDATE address SET street_name = ? WHERE AddressId = (SELECT AddressId FROM customer WHERE CustomerId = ?)", address.getStreet_name(), id);
            }
            if (address.getCity() != null) {
                jdbcTemplate.update("UPDATE address SET city = ? WHERE AddressId = (SELECT AddressId FROM customer WHERE CustomerId = ?)", address.getCity(), id);
            }
            if (address.getState() != null) {
                jdbcTemplate.update("UPDATE address SET state = ? WHERE AddressId = (SELECT AddressId FROM customer WHERE CustomerId = ?)", address.getState(), id);
            }
            if (address.getZip() != null) {
                jdbcTemplate.update("UPDATE address SET zip = ? WHERE AddressId = (SELECT AddressId FROM customer WHERE CustomerId = ?)", address.getZip(), id);
            }
        }

        // get the updated info from the customer to return
        return this.findById(id);
    }

    public boolean existsByEmail(String email) {
        List<Customer> c = jdbcTemplate.query("SELECT * FROM Customer WHERE email = ?", new Object[] {email}, (new BeanPropertyRowMapper<>(Customer.class)));
        return !c.isEmpty();
    }

    public Customer findByEmail(String email) {
        Long id = jdbcTemplate.queryForObject("SELECT CustomerId FROM Customer WHERE email = ?", new Object[] {email}, Long.class);
        return this.findById(id);
    }
}
