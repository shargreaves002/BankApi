package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Withdrawal;
import com.bankApi.bankApi.repositories.WithdrawalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalService {

    @Autowired
    private WithdrawalsRepository withdrawalsRepository;

    public List<Withdrawal> findAllByAccountId(Long id){
        return withdrawalsRepository.findAllByAccountId(id);
    }

    public Optional<Withdrawal> findById(long id) {
        return withdrawalsRepository.findById(id);
    }

    public Withdrawal updateWithdrawal(Withdrawal withdrawal, long id) {
        Withdrawal withdrawalToUpdate = withdrawalsRepository.getOne(id);
        if (withdrawal.getType() != null) withdrawalToUpdate.setType(withdrawal.getType());
        if (withdrawal.getAmount() != null) withdrawalToUpdate.setAmount(withdrawal.getAmount());
        if (withdrawal.getTransaction_date() != null) withdrawalToUpdate.setTransaction_date(withdrawal.getTransaction_date());
        if (withdrawal.getStatus() != null) withdrawalToUpdate.setStatus(withdrawal.getStatus());
        if (withdrawal.getMedium() != null) withdrawalToUpdate.setMedium(withdrawal.getMedium());
        if (withdrawal.getDescription() != null) withdrawalToUpdate.setDescription(withdrawal.getDescription());
        withdrawalsRepository.save(withdrawalToUpdate);
        return withdrawalToUpdate;
    }

    public void deleteById(Long id) {
        withdrawalsRepository.deleteById(id);
    }

    public Withdrawal createWithdrawal(Withdrawal withdrawal, Long id) {
        Withdrawal submit = new Withdrawal();
        submit.setAmount(withdrawal.getAmount());
        submit.setDescription(withdrawal.getDescription());
        submit.setId(withdrawal.getId());
        submit.setMedium(withdrawal.getMedium());
        submit.setTransaction_date(withdrawal.getTransaction_date());
        submit.setType(withdrawal.getType());
        submit.setStatus(withdrawal.getStatus());
        submit.setAccountId(id);

        withdrawalsRepository.save(submit);
        return submit;
    }

    public boolean existsById(Long id) {
        return withdrawalsRepository.existsById(id);
    }
}
