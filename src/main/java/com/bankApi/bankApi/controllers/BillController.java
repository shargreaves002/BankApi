package com.bankApi.bankApi.controllers;

import com.bankApi.bankApi.models.Account;
import com.bankApi.bankApi.services.AccountService;
import com.bankApi.bankApi.models.Bill;
import com.bankApi.bankApi.models.Response;
import com.bankApi.bankApi.services.BillService;
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
@CrossOrigin
public class BillController {

    private BillService billService;

    private AccountService accountService;

    private CustomerService customerService;

    @Autowired
    public BillController (BillService billService, AccountService accountService, CustomerService customerService) {
        Assert.notNull(billService, "Bill service must not be null.");
        Assert.notNull(accountService, "Account service must not be null.");
        Assert.notNull(customerService, "Customer service must not be null.");
        this.billService = billService;
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @GetMapping("/accounts/{accountID}/bills")
    public ResponseEntity<?> getAllBillsForAcc(@PathVariable("accountID") Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (!accountService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Account with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            List<Bill> bills = billService.findAllByAccountId(id);
            response.setCode(200);
            response.setData(bills);
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @GetMapping("/bills/{billId}")
    public ResponseEntity<?> getBillById(@PathVariable("billId") Long id) {
        HttpStatus statusCode;
        Response response = new Response();
        if (!billService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Bill with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        } else {
            Bill bill = billService.findById(id);
            response.setCode(200);
            response.setData(new ArrayList<>(Collections.singleton(bill)));
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }



    @GetMapping("/customers/{customerId}/bills")
    public ResponseEntity<?> getAllBillsForCus(@PathVariable("customerId") Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (!customerService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Customer with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            List<Account> accounts = accountService.findAllByCustomerId(id);
            List<Bill> bills = new ArrayList<>();
            for (Account i : accounts) {
                bills.addAll(billService.findAllByAccountId(i.getId()));
            }
            response.setCode(200);
            response.setData(bills);
            statusCode = HttpStatus.OK;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @PostMapping("/accounts/{accountId}/bills")
    public ResponseEntity<?> createBill(@RequestBody Bill bill, @PathVariable("accountId") Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (!accountService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Account with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            response.setCode(201);
            Bill createdBill = billService.createBill(bill, id);
            response.setData(new ArrayList<>(Collections.singleton(createdBill)));
            statusCode = HttpStatus.CREATED;
        }
        return new ResponseEntity<>(response, statusCode);
    }


    @PutMapping("/bills/{billId}")
    public ResponseEntity<?> updateBill(@PathVariable("billId") Long id, @RequestBody Bill bill) {
        Response response = new Response();
        HttpStatus statusCode;
        if (!billService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Bill with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            Bill updatedBill = billService.updateBill(id, bill);
            response.setCode(202);
            response.setData(Collections.singletonList(updatedBill));
            statusCode = HttpStatus.ACCEPTED;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @DeleteMapping("/bills/{billId}")
    public ResponseEntity<?> deleteBill(@PathVariable("billId") Long id) {
        Response response = new Response();
        HttpStatus statusCode;
        if (!billService.existsById(id)) {
            response.setCode(404);
            response.setMessage("Bill with ID " + id + " not found.");
            statusCode = HttpStatus.NOT_FOUND;
        }else{
            billService.deleteBillById(id);
            statusCode = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(response, statusCode);
    }
}
