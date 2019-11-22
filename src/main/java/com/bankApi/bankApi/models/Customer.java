package com.bankApi.bankApi.models;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Customer {
    private Long CustomerId;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private Address address;

    public Long getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(Long customerId) {
        CustomerId = customerId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setAddress(Address address){
        this.address = address;
    }

    public Address getAddress(){
        return this.address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
