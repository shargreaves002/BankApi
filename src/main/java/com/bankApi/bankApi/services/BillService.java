package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Bill;
import com.bankApi.bankApi.repositories.BillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    @Autowired
    private BillsRepository billsRepository;

    public boolean existsById(Long id) {
        return billsRepository.existsById(id);
    }

    public Bill createBill(Bill bill, long id) {
        bill.setAccountId(id);
        billsRepository.save(bill);
        return bill;
    }

    public Bill updateBill(Long id, Bill bill) {
        Bill billToUpdate = billsRepository.getOne(id);
        if (bill.getNickname() != null) billToUpdate.setNickname(bill.getNickname());
        if (bill.getPaymentDate() != null) billToUpdate.setPaymentDate(bill.getPaymentDate());
        if (bill.getRecurringDate() != null) billToUpdate.setRecurringDate(bill.getRecurringDate());
        if (bill.getUpcomingPaymentDate() != null) billToUpdate.setUpcomingPaymentDate(bill.getUpcomingPaymentDate());
        if (bill.getPaymentAmount() != null) billToUpdate.setPaymentAmount(bill.getPaymentAmount());
        if (bill.getStatus() != null) billToUpdate.setStatus(bill.getStatus());
        billsRepository.save(billToUpdate);
        return billToUpdate;
    }

    public void deleteBillById(Long id) {
        billsRepository.deleteById(id);
    }

    public List<Bill> findAllByAccountId(Long id) {
        return billsRepository.findAllByAccountId(id);
    }

    public Optional<Bill> findById(Long id) {
        return billsRepository.findById(id);
    }
}
