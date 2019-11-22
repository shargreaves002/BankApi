package com.bankApi.bankApi.controllers;

import com.bankApi.bankApi.models.Customer;
import com.bankApi.bankApi.models.Response;
import com.bankApi.bankApi.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class CustomerController {

    private CustomerService customerService;

    @Autowired
    public CustomerController (CustomerService customerService) {
        Assert.notNull(customerService, "Customer Service must not be null.");
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers(){
        Response response=new Response();
        HttpStatus statusCode;
        List<Customer> c = customerService.findAll();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(c);
        statusCode = HttpStatus.OK;
        return new ResponseEntity<>(response, statusCode);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable long id) {
        HttpStatus statusCode;
        Response response = new Response();
        if (!customerService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Error fetching account: " + id);
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            Customer c = customerService.findById(id);
            response.setCode(200);
            response.setMessage("Success");
            response.setData(new ArrayList<>(Collections.singleton(c)));
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer){
        Response response= new Response();
        response.setCode(201);
        response.setMessage("Customer account created");
        Customer savedCustomer = customerService.save(customer);
        response.setData(new ArrayList<>(Collections.singleton(savedCustomer)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer, @PathVariable long id) {
        Response response= new Response();
        HttpStatus statusCode;
        if (customerService.existsById(id)) {
            Customer updatedCustomer = customerService.updateCustomer(customer, id);
            response.setData(Collections.singletonList(updatedCustomer));
            response.setCode(202);
            statusCode = HttpStatus.ACCEPTED;
        } else {
            response.setCode(404);
            response.setMessage("Customer with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, statusCode);
    }
}
