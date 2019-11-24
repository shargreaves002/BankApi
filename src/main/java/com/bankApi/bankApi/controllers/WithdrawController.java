package com.bankApi.bankApi.controllers;

import com.bankApi.bankApi.models.Response;
import com.bankApi.bankApi.models.Withdraw;
import com.bankApi.bankApi.services.AccountService;
import com.bankApi.bankApi.services.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
public class WithdrawController {

    private WithdrawService withdrawService;

    private AccountService accountService;

    @Autowired
    public WithdrawController(WithdrawService withdrawService, AccountService accountService){
        Assert.notNull(withdrawService, "Withdraw Service must not be null.");
        Assert.notNull(accountService, "Account Service must not be null.");
        this.withdrawService = withdrawService;
        this.accountService = accountService;
    }

    @GetMapping("/withdraws/{id}")
    public ResponseEntity<?> getWithdrawById(@PathVariable long id){
        Response response = new Response();
        HttpStatus statusCode;
        if (withdrawService.existsById(id)) {
            Withdraw w = withdrawService.findById(id);
            response.setCode(200);
            response.setData(new ArrayList<>(Collections.singleton(w)));
            statusCode = HttpStatus.OK;
        } else {
            response.setCode(404);
            response.setMessage("Withdraw with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @GetMapping("/accounts/{id}/withdraws")
    public ResponseEntity<?> getWithdrawsForAccount(@PathVariable Long id){
        Response response = new Response();
        HttpStatus statusCode;
        if (accountService.existsById(id)) {
            response.setCode(201);
            List<Withdraw> w = withdrawService.findAllByAccountId(id);
            response.setData(w);
            statusCode = HttpStatus.OK;
        } else {
            response.setCode(404);
            response.setMessage("Account with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @PostMapping("/accounts/{id}/withdraws")
    public ResponseEntity<?> createWithdraw(@RequestBody Withdraw withdraw, @PathVariable Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (accountService.existsById(id)) {
            response.setCode(201);
            Withdraw createdWithdraw = withdrawService.createWithdraw(withdraw, id);
            response.setData(new ArrayList<>(Collections.singleton(createdWithdraw)));
            statusCode = HttpStatus.CREATED;
        } else {
            response.setCode(404);
            response.setMessage("Account with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @PutMapping("/withdraws/{id}")
    public ResponseEntity<?> updateWithdraw(@RequestBody Withdraw withdraw, @PathVariable Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (withdrawService.existsById(id)) {
            Withdraw updatedWithdraw = withdrawService.updateWithdraw(withdraw, id);
            response.setCode(202);
            response.setData(Collections.singletonList(updatedWithdraw));
            statusCode = HttpStatus.ACCEPTED;
        } else {
            response.setMessage("Withdraw with ID " + id + " not found.");
            response.setCode(404);
            statusCode = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @DeleteMapping("/withdraws/{id}")
    public ResponseEntity<?> deleteWithdraw(@PathVariable Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (withdrawService.existsById(id)) {
            withdrawService.deleteById(id);
            statusCode = HttpStatus.NO_CONTENT;
        } else {
            response.setMessage("Withdraw with ID " + id + " not found.");
            response.setCode(404);
            statusCode = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(response, statusCode);
    }
}
