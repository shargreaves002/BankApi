package com.bankApi.bankApi.controllers;

import com.bankApi.bankApi.models.Account;
import com.bankApi.bankApi.models.Customer;
import com.bankApi.bankApi.models.Response;
import com.bankApi.bankApi.services.AccountService;
import com.bankApi.bankApi.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(){
        HttpStatus statusCode;
        Response response = new Response();
        List<Account> r = accountService.findAll();
//        To check if a list is empty add .isEmpty to your method.
        if(r.isEmpty()){
            response.setCode(404);
            response.setMessage("error fetching accounts");
//            StatusCode for 404.
            statusCode = HttpStatus.NOT_FOUND;
        }else {
            response.setCode(200);
            response.setMessage("Success");
            response.setData(r);
//            StatusCode for 200.
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<?> List(@PathVariable Long id ) {
        HttpStatus statusCode;
        Response response = new Response();
        if (!accountService.existsById(id)) {
            response.setCode(404);
            response.setMessage("error fetching account");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            Optional<Account> a = accountService.findById(id);
            response.setCode(200);
            response.setMessage("error fetching account");
//            The singletonList() method of java.util.Collections class is used to return an immutable list containing only the specified object.
            response.setData(Collections.singletonList(a));
            statusCode = HttpStatus.OK;

        }
        return new ResponseEntity<>(response, statusCode);
    }

    @GetMapping("/customers/{id}/accounts")
    public ResponseEntity<?> getAccountsForCustomer(@PathVariable Long id){
        HttpStatus statusCode;
        Response response = new Response();
        if(!customerService.existsById(id)){
            response.setCode(404);
            response.setMessage("error fetching customers account");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
//            Trying to find customers by ID.
            List<Account> y = accountService.findAllByCustomerId(id);
            response.setData(y);
            response.setCode(200);
            response.setMessage("Success");
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @PostMapping("/customers/{id}/accounts")
    public ResponseEntity<?> createAccount(@RequestBody Account account, @PathVariable("id") long id){
        Response response = new Response();
        HttpStatus statusCode;
        if(!customerService.existsById(id)){
            response.setCode(404);
            response.setMessage("error creating customers account");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            Customer customer = customerService.findById(id);
            account.setCustomerId(id);
            accountService.save(account);
            response.setCode(201);
            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(account);
            response.setData(accounts);
            response.setMessage("Account created");
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }
    @PutMapping("/accounts/{id}")
    public ResponseEntity<?> updateAccounts(@RequestBody Account account, @PathVariable("id") long id){
        Response response = new Response();
        HttpStatus statusCode;
        Optional<Account> g = accountService.findById(id);
        if(!accountService.existsById(id)){
            response.setCode(404);
            response.setMessage("Error");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            accountService.updateAccount(account, id);
            response.setCode(200);
            response.setMessage("Customer account updated");
            response.setData(Collections.singletonList(g));
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccounts(@PathVariable("id") long id){
        Response response = new Response();
        HttpStatus statusCode;
        if(!accountService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Account does not exists");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            accountService.deleteById(id);
            response.setCode(202);
            response.setMessage("Accounted successfully deleted");
            statusCode = HttpStatus.ACCEPTED;
        }
        return new ResponseEntity<>(response, statusCode);
    }
}
