package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BillService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BillService(JdbcTemplate jdbcTemplate){
        Assert.notNull(jdbcTemplate,"jdbcTemplate must not be null");
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsById(Long id) {
       List<Bill> c = jdbcTemplate.query("SELECT * FROM Bill WHERE BillId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Bill.class)));
       return !c.isEmpty();
    }

    public Bill createBill(Bill bill, long id) {
        Timestamp time = new Timestamp(new java.util.Date().getTime());
        jdbcTemplate.update("INSERT INTO bill (nickname, creationDate, paymentDate, recurringDate, upcomingPaymentDate, paymentAmount, accountId, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        bill.getNickname(),time,bill.getPaymentDate(),bill.getRecurringDate(),bill.getUpcomingPaymentDate(),bill.getPaymentAmount(),id, bill.getStatus().toString());
        Long billId = jdbcTemplate.queryForObject("SELECT MAX(BillId) FROM Bill WHERE accountId = ?",new Object[]{bill.getAccountId()}, Long.class);
        bill.setId(billId);
        return bill;
    }

    public Bill updateBill(Long id, Bill bill) {
        if (bill.getStatus() != null) {
            jdbcTemplate.update("UPDATE Bill SET status = ? WHERE BillId = ?", bill.getStatus().toString(), id);
        }
        if (bill.getNickname() != null){
            jdbcTemplate.update("UPDATE Bill SET nickname = ? WHERE BillId = ?", bill.getNickname(), id);
        }
        if (bill.getPaymentDate() != null){
            jdbcTemplate.update("UPDATE Bill SET paymentDate = ? WHERE BillId = ?", bill.getPaymentDate(), id);
        }
        if (bill.getRecurringDate() != null){
            jdbcTemplate.update("UPDATE Bill SET recurringDate = ? WHERE BillId = ?", bill.getRecurringDate(), id);
        }
        if (bill.getUpcomingPaymentDate() != null){
            jdbcTemplate.update("UPDATE Bill SET upcomingPaymentDate = ? WHERE BillId = ?", bill.getUpcomingPaymentDate(), id);
        }
        if (bill.getPaymentAmount() != null){
            jdbcTemplate.update("UPDATE Bill SET paymentAmount = ? WHERE BillId = ?", bill.getPaymentAmount(), id);
        }
        return this.findById(id);
    }

    public void deleteBillById(Long id) {
            jdbcTemplate.update("DELETE FROM bill WHERE BillId = ?", id);
    }

    public List<Bill> findAllByAccountId(Long id) {
        List<Long> billIds = jdbcTemplate.queryForList("SELECT BillId FROM Bill WHERE AccountId = ?", new Object[] {id}, Long.class);
        List<Bill> bills = new ArrayList<>();
        if (billIds != null){
            billIds.forEach(v -> {
                Bill bill = this.findById(v);
                bill.setId(v);
                bills.add(bill);
            });
        }
        return bills;
    }

    public Bill findById(Long id) {
        Bill bill = jdbcTemplate.query("SELECT * FROM Bill WHERE BillId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Bill.class))).get(0);
        Timestamp time = jdbcTemplate.queryForObject("SELECT creationDate FROM Bill WHERE BillId = ?", new Object[]{id}, Timestamp.class);
        bill.setCreation_date(String.format("%1$TD", time));
        return bill;
    }
}
