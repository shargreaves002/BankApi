package com.bankApi.bankApi.controllers;

import com.bankApi.bankApi.models.Customer;
import com.bankApi.bankApi.models.Response;
import com.bankApi.bankApi.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class CustomerController {
    @Autowired
    private CustomerService customerService;

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
        Optional<Customer> c = customerService.findById(id);
        if (!c.isPresent()) {
            response.setCode(404);
            response.setMessage("Error fetching account: " + id);
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            response.setCode(200);
            response.setMessage("Success");
            response.setData(new ArrayList<>(Collections.singleton(c.get())));
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) throws IOException {
        Response response= new Response();
        response.setCode(201);
        response.setMessage("Customer account created");
        response.setData(new ArrayList<>(Collections.singleton(customer)));
        customerService.save(customer);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //TODO: PUT mapping for /customer/{id}
}
