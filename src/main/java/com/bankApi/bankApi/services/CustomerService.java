package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Customer;
import com.bankApi.bankApi.repositories.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomersRepository customersRepository;

    public boolean existsById(Long id) {
        return customersRepository.existsById(id);
    }

    public Optional<Customer> findById(Long id) {
        return customersRepository.findById(id);
    }

    public List<Customer> findAll() {
        return customersRepository.findAll();
    }

    public void save(Customer customer) {
        customersRepository.save(customer);
    }
}
