package com.bankApi.bankApi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
<<<<<<< HEAD
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

import javax.persistence.*;
import java.util.Date;
=======

import javax.persistence.*;
import java.sql.Timestamp;
>>>>>>> 47583879a8ed539fd5550d29a38a9cfb4099e23b

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Deposit {


    private Long id;

    @Enumerated(EnumType.STRING)
    private DepositType type;

<<<<<<< HEAD
    private Timestamp transaction_date;
=======
    private String transaction_date;
>>>>>>> 47583879a8ed539fd5550d29a38a9cfb4099e23b

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Long accountId;

    @Enumerated(EnumType.STRING)
    private TransactionMedium medium;

    private Double amount;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DepositType getType() {
        return type;
    }

    public void setType(DepositType type) {
        this.type = type;
    }

    public Timestamp getTransaction_date() {
        return transaction_date;
    }

<<<<<<< HEAD
    public void setTransaction_date(Timestamp transaction_date) {
=======
    public void setTransaction_date(String transaction_date) {
>>>>>>> 47583879a8ed539fd5550d29a38a9cfb4099e23b
        this.transaction_date = transaction_date;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TransactionMedium getMedium() {
        return medium;
    }

    public void setMedium(TransactionMedium medium) {
        this.medium = medium;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
