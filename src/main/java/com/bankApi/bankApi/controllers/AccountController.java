package com.bankApi.bankApi.controllers;

import com.bankApi.bankApi.models.Account;
import com.bankApi.bankApi.models.Customer;
import com.bankApi.bankApi.models.Response;
import com.bankApi.bankApi.services.AccountService;
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
public class AccountController {
    private AccountService accountService;

    private CustomerService customerService;

    @Autowired
    public AccountController(AccountService accountService, CustomerService customerService) {
        Assert.notNull(accountService, "Account Service must not be null.");
        Assert.notNull(customerService, "Customer Service must not be null.");
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(){
        HttpStatus statusCode;
        Response response = new Response();
        List<Account> r = accountService.findAll();
        if(r.isEmpty()){
            response.setCode(404);
            response.setMessage("No accounts found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else {
            response.setCode(200);
            response.setMessage("Success");
            response.setData(r);
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
            response.setMessage("No account with ID " + id + " Found.");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            Account a = accountService.findById(id);
            response.setCode(200);
            response.setMessage("Success");
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
            response.setMessage("No customer with ID " + id + " Found.");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
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
            response.setMessage("No customer with ID " + id + " Found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            Customer customer = customerService.findById(id);
            account.setCustomerId(customer.getCustomerId());
            Account savedAccount = accountService.save(account);
            response.setCode(201);
            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(savedAccount);
            response.setData(accounts);
            response.setMessage("Account created.");
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }
    @PutMapping("/accounts/{id}")
    public ResponseEntity<?> updateAccounts(@RequestBody Account account, @PathVariable("id") long id){
        Response response = new Response();
        HttpStatus statusCode;
        if(!accountService.existsById(id)){
            response.setCode(404);
            response.setMessage("No account with ID " + id + " Found.");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            Account g = accountService.updateAccount(account, id);
            response.setCode(202);
            response.setMessage("Account updated");
            response.setData(Collections.singletonList(g));
            statusCode = HttpStatus.ACCEPTED;
        }
        return new ResponseEntity<>(response, statusCode);
    }
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccounts(@PathVariable("id") long id){
        Response response = new Response();
        HttpStatus statusCode;
        if(!accountService.existsById(id)) {
            response.setCode(404);
            response.setMessage("No account with ID " + id + " Found.");
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
