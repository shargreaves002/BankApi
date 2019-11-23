package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.Account;
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
public class AccountService {

        private JdbcTemplate jdbcTemplate;

        @Autowired
        public AccountService(JdbcTemplate jdbcTemplate) {
            Assert.notNull(jdbcTemplate, "Jdbc Template must not be null.");
            this.jdbcTemplate = jdbcTemplate;
        }

        public Account findById(long id){
            Account account =  jdbcTemplate.query("SELECT * FROM Account WHERE AccountId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Account.class))).get(0);
            account.setId(id);
            return account;
        }

        public boolean existsById(long id) {
            List<Account> c = jdbcTemplate.query("SELECT * FROM Account WHERE AccountId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Account.class)));
            return !c.isEmpty();
        }

        public void deleteById(long id) {
            jdbcTemplate.update("DELETE FROM account WHERE AccountId = ?", id);
        }

        public Account save(Account account) {
            jdbcTemplate.update("INSERT INTO account (type, nickname, rewards, balance, CustomerId) VALUES (?, ?, ?, ?, ?)",
                    account.getType().toString(), account.getNickname(), account.getRewards(), account.getBalance(), account.getCustomerId());
            Long id = jdbcTemplate.queryForObject("SELECT AccountId FROM Account WHERE nickname = ?", new Object[] {account.getNickname()}, Long.class);
            account.setId(id);
            return account;
        }

        public List<Account> findAllByCustomerId(Long id) {
            List<Long> accountIds =  jdbcTemplate.queryForList("SELECT AccountId FROM account WHERE CustomerId = ?", new Object[] {id}, Long.class);
            List<Account> accounts = new ArrayList<>();
            accountIds.forEach(v -> accounts.add(this.findById(v)));
            return accounts;
        }

        public List<Account> findAll() {
            List<Long> accountIds =  jdbcTemplate.queryForList("SELECT AccountId FROM account", Long.class);
            List<Account> accounts = new ArrayList<>();
            accountIds.forEach(v -> accounts.add(this.findById(v)));
            return accounts;
        }

        public Account updateAccount(Account account, Long id) {
            if (account.getType() != null) {
                jdbcTemplate.update("UPDATE Account SET type = ? WHERE AccountId = ?", account.getType().toString(), id);
            }
            if (account.getNickname() != null) {
                jdbcTemplate.update("UPDATE Account SET nickname = ? WHERE AccountId = ?", account.getNickname(), id);
            }
            if (account.getBalance() != null) {
                jdbcTemplate.update("UPDATE Account SET balance = ? WHERE AccountId = ?", account.getBalance(), id);
            }
            if (account.getRewards() != null) {
                jdbcTemplate.update("UPDATE Account SET rewards = ? WHERE AccountId = ?", account.getRewards(), id);
            }
            return this.findById(id);
        }
}
