package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepositService {

    public List<Deposit> findAllByAccountId(Long id){
        return null; // depositsRepository.findAllByAccountId(id);
    }

    public Optional<Deposit> findById(long id) {
        return Optional.empty(); //depositsRepository.findById(id);
    }

    public Deposit updateDeposit(Deposit deposit, long id) {
        /*Deposit depositToUpdate = depositsRepository.getOne(id);
        if (deposit.getType() != null) depositToUpdate.setType(deposit.getType());
        if (deposit.getAmount() != null) depositToUpdate.setAmount(deposit.getAmount());
        if (deposit.getStatus() != null) depositToUpdate.setStatus(deposit.getStatus());
        if (deposit.getMedium() != null) depositToUpdate.setMedium(deposit.getMedium());
        if (deposit.getDescription() != null) depositToUpdate.setDescription(deposit.getDescription());
        depositsRepository.save(depositToUpdate);*/
        return null; // depositToUpdate;
    }

    public void deleteById(Long id) {
        // depositsRepository.deleteById(id);
    }

    public Deposit createDeposit(Deposit deposit, Long id) {
        Deposit submit = new Deposit();
        submit.setAmount(deposit.getAmount());
        submit.setDescription(deposit.getDescription());
        submit.setId(deposit.getId());
        submit.setMedium(deposit.getMedium());
        submit.setTransaction_date(deposit.getTransaction_date());
        submit.setType(deposit.getType());
        submit.setStatus(deposit.getStatus());
        submit.setAccountId(id);

        // depositsRepository.save(submit);
        return submit;
    }

    public boolean existsById(Long id) {
        return true; //depositsRepository.existsById(id);
    }
}
