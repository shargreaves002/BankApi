package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BillService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BillService(JdbcTemplate jdbcTemplate){
        Assert.notNull(jdbcTemplate,"jdbcTemplate must not be null");
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsById(Long id) {
       List<Bill> c = jdbcTemplate.query("SELECT * FROM Bill WHERE BillId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Bill.class)));
       return !c.isEmpty();
    }

    public Bill createBill(Bill bill, long id) {
      jdbcTemplate.update("INSERT INTO bill (nickname, creation_date, paymentDate, recurringDate, upcomingPaymentDate, paymentAmount, accountId, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
      bill.getNickname(),bill.getCreation_date(),bill.getPaymentDate(),bill.getRecurringDate(),bill.getUpcomingPaymentDate(),bill.getPaymentAmount(),id, bill.getStatus());
      Long billId = jdbcTemplate.queryForObject("SELECT BillId FROM Bill WHERE creation_date = ? and accountId = ?",new Object[]{bill.getCreation_date(), bill.getAccountId()}, Long.class);

      bill.setId(billId);
      return bill;
    }

    public Bill updateBill(Long id, Bill bill) {
        if (bill.getStatus() != null) {
            jdbcTemplate.update("UPDATE Bill SET status = ? WHERE BillId = ?", bill.getStatus(), id);
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
        return jdbcTemplate.query("SELECT * FROM Bill WHERE BillId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Bill.class))).get(0);
    }
}
