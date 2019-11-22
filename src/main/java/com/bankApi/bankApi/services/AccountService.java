package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Account;
import com.bankApi.bankApi.repositories.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {


        @Autowired
        private AccountsRepository accountsRepository;

        @Autowired
        private AccountService accountService;

        public Optional<Account> findById(long id){
            return accountsRepository.findById(id);
        }

        public boolean existsById(long id) {
            return accountsRepository.existsById(id);
        }

        public void deleteById(long id) {
            accountsRepository.deleteById(id);
        }

        public void save(Account commit) {
            accountsRepository.save(commit);
        }

        public List<Account> findAllByCustomerId(Long id) {
            return accountsRepository.findAllByCustomerId(id);
        }

        public List<Account> findAll() {
            return accountsRepository.findAll();
        }

        public void updateAccount(Account account, long id) {
            Account accountToUpdate = accountsRepository.getOne(id);
            if (account.getType() != null) accountToUpdate.setType(account.getType());
            if (account.getNickname() != null) accountToUpdate.setNickname(account.getNickname());
            if (account.getBalance() != null) accountToUpdate.setBalance(account.getBalance());
            if (account.getRewards() != null) accountToUpdate.setRewards(account.getRewards());

            accountsRepository.save(accountToUpdate);

        }
}
