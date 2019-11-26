package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.*;
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
public class DepositService {

    private JdbcTemplate jdbcTemplate;
    private AccountService accountService;

    @Autowired
    public DepositService(JdbcTemplate jdbcTemplate, AccountService accountService) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate must not be null.");
        Assert.notNull(accountService,"account service must not be null");
        this.jdbcTemplate = jdbcTemplate;
        this.accountService = accountService;

    }


    public List<Deposit> findAllByAccountId(Long id){
        List<Long> depositIds = jdbcTemplate.queryForList("SELECT DepositId FROM Deposit WHERE AccountId = ?", new Object[] {id}, Long.class);
        List<Deposit> deposits = new ArrayList<>();
        if (depositIds != null){
            depositIds.forEach(v -> {
                Deposit deposit = this.findById(v);
                deposit.setId(v);
                deposits.add(deposit);
            });
        }
        return deposits;
    }

    public Deposit findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM Deposit WHERE DepositId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Deposit.class))).get(0); //depositsRepository.findById(id);
    }

    public Deposit updateDeposit(Deposit deposit, long id) {
        if (deposit.getType() != null) {
            jdbcTemplate.update("UPDATE Deposit SET type = ? WHERE DepositId = ?", deposit.getType(), id);
        }
        if (deposit.getAmount() != null) {
            TransactionStatus currentStatus = jdbcTemplate.queryForObject("SELECT status FROM Deposit WHERE DepositId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionStatus.class));
            TransactionMedium currentMedium = jdbcTemplate.queryForObject("SELECT Medium FROM Deposit WHERE DepositId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionMedium.class));
            if (currentStatus == TransactionStatus.Completed && currentMedium == TransactionMedium.Balance){
                Long accountId = jdbcTemplate.queryForObject("SELECT AccountId FROM Deposit WHERE DepositId = ?", new Object[] {id}, Long.class);
                Double currentAmount = jdbcTemplate.queryForObject("Select Amount FROM Deposit WHERE DepositId = ?", new Object[] {id}, Double.class);
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = ?", new Object[] {accountId}, Double.class);
                if (currentAmount == null) currentAmount = 0.0;
                Double amountToChange = deposit.getAmount() - currentAmount;
                balance += amountToChange;
                Account account = new Account();
                account.setBalance(balance);
                accountService.updateAccount(account, accountId);
            }
            jdbcTemplate.update("UPDATE Deposit SET amount = ? WHERE DepositId = ?", deposit.getAmount(), id);
        }
        if (deposit.getStatus() != null) {
            TransactionStatus currentStatus = jdbcTemplate.queryForObject("SELECT status FROM Deposit WHERE DepositId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionStatus.class));
            Double depositAmount = jdbcTemplate.queryForObject("SELECT Amount FROM Deposit WHERE DepositId = ?", new Object[]{id}, Double.class);
            if (deposit.getStatus() == TransactionStatus.Completed && currentStatus != TransactionStatus.Completed){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance += depositAmount;
                Account accounts = new Account();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            } else if (deposit.getStatus() != TransactionStatus.Completed && currentStatus == TransactionStatus.Completed){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance -= depositAmount;
                Account accounts = new Account();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            }
            jdbcTemplate.update("UPDATE Deposit SET status = ? WHERE DepositId = ?", deposit.getStatus(), id);
        }
        if (deposit.getMedium() != null) {
            Double depositAmount = jdbcTemplate.queryForObject("SELECT Amount FROM Deposit WHERE DepositId = ?", new Object[]{id}, Double.class);
            TransactionMedium currentMedium = jdbcTemplate.queryForObject("SELECT Medium FROM Deposit WHERE DepositId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionMedium.class));
            if (deposit.getMedium() == TransactionMedium.Balance && currentMedium != TransactionMedium.Balance){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance += depositAmount;
                Account accounts = new Account ();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            } else if (deposit.getMedium() != TransactionMedium.Balance && currentMedium == TransactionMedium.Balance){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance -= depositAmount;
                Account accounts = new Account();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            }
            jdbcTemplate.update("UPDATE Deposit SET medium = ? WHERE DepositId = ?", deposit.getMedium(), id);
        }
        if (deposit.getDescription() != null) {
            jdbcTemplate.update("UPDATE Deposit SET description = ? WHERE DepositId = ?", deposit.getDescription(), id);
        }
        return this.findById(id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM deposit WHERE DepositId = ?", id);
    }

    public Deposit createDeposit(Deposit deposit, Long id) {
        jdbcTemplate.update("INSERT INTO deposit (type, status, medium, date, accountId, amount, description) VALUES (?, ?, ?, ?, ?,?,?)",
              deposit.getType(),deposit.getStatus(),deposit.getMedium(),deposit.getTransaction_date(),id,deposit.getAmount(),deposit.getDescription());
        Long depositId = jdbcTemplate.queryForObject("SELECT DepositId FROM Deposit WHERE date = ? and accountId = ?", new Object[] {deposit.getTransaction_date(), deposit.getAccountId()}, Long.class);

        if(deposit.getMedium()== TransactionMedium.Balance && deposit.getStatus() == TransactionStatus.Completed){
            Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = ?", new Object[] {id}, Double.class);
            balance += deposit.getAmount();
            Account accounts = new Account ();
            accounts.setBalance(balance);
            accountService.updateAccount(accounts, id);
        }
        deposit.setId(depositId);
        return deposit;
    }

    public boolean existsById(Long id) {
        List<Deposit> d= jdbcTemplate.query("SELECT * FROM Deposit WHERE DepositId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Deposit.class)));
        return !d.isEmpty();
    }
}
