package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawService {

    private JdbcTemplate jdbcTemplate;
    private AccountService accountService;

    @Autowired
    public WithdrawService(JdbcTemplate jdbcTemplate, AccountService accountService) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate must not be null.");
        Assert.notNull(accountService,"account service must not be null");
        this.jdbcTemplate = jdbcTemplate;
        this.accountService = accountService;

    }


    public List<Withdraw> findAllByAccountId(Long id){
        List<Long> WithdrawIds = jdbcTemplate.queryForList("SELECT WithdrawId FROM Withdraw WHERE AccountId = ?", new Object[] {id}, Long.class);
        List<Withdraw> Withdraws = new ArrayList<>();
        if (WithdrawIds != null){
            WithdrawIds.forEach(v -> {
                Withdraw Withdraw = this.findById(v);
                Withdraw.setId(v);
                Withdraws.add(Withdraw);
            });
        }
        return Withdraws;
    }

    public Withdraw findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM Withdraw WHERE WithdrawId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Withdraw.class))).get(0); //WithdrawsRepository.findById(id);
    }

    public Withdraw updateWithdraw(Withdraw Withdraw, long id) {
        if (Withdraw.getType() != null) {
            jdbcTemplate.update("UPDATE Withdraw SET type = ? WHERE WithdrawId = ?", Withdraw.getType(), id);
        }
        if (Withdraw.getAmount() != null) {
            TransactionStatus currentStatus = jdbcTemplate.queryForObject("SELECT status FROM Withdraw WHERE WithdrawId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionStatus.class));
            TransactionMedium currentMedium = jdbcTemplate.queryForObject("SELECT Medium FROM Withdraw WHERE WithdrawId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionMedium.class));
            if (currentStatus == TransactionStatus.Completed && currentMedium == TransactionMedium.Balance){
                Long accountId = jdbcTemplate.queryForObject("SELECT AccountId FROM Withdraw WHERE WithdrawId = ?", new Object[] {id}, Long.class);
                Double currentAmount = jdbcTemplate.queryForObject("Select Amount FROM Withdraw WHERE WithdrawId = ?", new Object[] {id}, Double.class);
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = ?", new Object[] {accountId}, Double.class);
                if (currentAmount == null) currentAmount = 0.0;
                Double amountToChange = Withdraw.getAmount() - currentAmount;
                balance += amountToChange;
                Account account = new Account();
                account.setBalance(balance);
                accountService.updateAccount(account, accountId);
            }
            jdbcTemplate.update("UPDATE Withdraw SET amount = ? WHERE WithdrawId = ?", Withdraw.getAmount(), id);
        }
        if (Withdraw.getStatus() != null) {
            TransactionStatus currentStatus = jdbcTemplate.queryForObject("SELECT status FROM Withdraw WHERE WithdrawId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionStatus.class));
            Double WithdrawAmount = jdbcTemplate.queryForObject("SELECT Amount FROM Withdraw WHERE WithdrawId = ?", new Object[]{id}, Double.class);
            if (Withdraw.getStatus() == TransactionStatus.Completed && currentStatus != TransactionStatus.Completed){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Withdraw WHERE WithdrawId = ?)", new Object[] {id}, Double.class);
                balance += WithdrawAmount;
                Account accounts = new Account();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            } else if (Withdraw.getStatus() != TransactionStatus.Completed && currentStatus == TransactionStatus.Completed){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Withdraw WHERE WithdrawId = ?)", new Object[] {id}, Double.class);
                balance -= WithdrawAmount;
                Account accounts = new Account();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            }
            jdbcTemplate.update("UPDATE Withdraw SET status = ? WHERE WithdrawId = ?", Withdraw.getStatus(), id);
        }
        if (Withdraw.getMedium() != null) {
            Double WithdrawAmount = jdbcTemplate.queryForObject("SELECT Amount FROM Withdraw WHERE WithdrawId = ?", new Object[]{id}, Double.class);
            TransactionMedium currentMedium = jdbcTemplate.queryForObject("SELECT Medium FROM Withdraw WHERE WithdrawId = ?", new Object[]{id}, new BeanPropertyRowMapper<>(TransactionMedium.class));
            if (Withdraw.getMedium() == TransactionMedium.Balance && currentMedium != TransactionMedium.Balance){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Withdraw WHERE WithdrawId = ?)", new Object[] {id}, Double.class);
                balance += WithdrawAmount;
                Account accounts = new Account ();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            } else if (Withdraw.getMedium() != TransactionMedium.Balance && currentMedium == TransactionMedium.Balance){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Withdraw WHERE WithdrawId = ?)", new Object[] {id}, Double.class);
                balance -= WithdrawAmount;
                Account accounts = new Account();
                accounts.setBalance(balance);
                accountService.updateAccount(accounts, id);
            }
            jdbcTemplate.update("UPDATE Withdraw SET medium = ? WHERE WithdrawId = ?", Withdraw.getMedium(), id);
        }
        if (Withdraw.getDescription() != null) {
            jdbcTemplate.update("UPDATE Withdraw SET description = ? WHERE WithdrawId = ?", Withdraw.getDescription(), id);
        }
        return this.findById(id);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM Withdraw WHERE WithdrawId = ?", id);
    }

    public Withdraw createWithdraw(Withdraw withdraw, Long id) {
        withdraw.setTransaction_date(new Timestamp(Long.parseLong(new java.util.Date().toString())).toString());
        jdbcTemplate.update("INSERT INTO Withdraw (type, status, medium, date, accountId, amount, description) VALUES (?, ?, ?, ?, ?,?,?)",
                withdraw.getType().toString(),withdraw.getStatus().toString(),withdraw.getMedium().toString(),withdraw.getTransaction_date(),id,withdraw.getAmount(),withdraw.getDescription());
        Long withdrawId = jdbcTemplate.queryForObject("SELECT MAX(WithdrawId) FROM Withdraw WHERE accountId = ?", new Object[] {withdraw.getAccountId()}, Long.class);

        if(withdraw.getMedium()== TransactionMedium.Balance && withdraw.getStatus() == TransactionStatus.Completed){
            Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = ?", new Object[] {id}, Double.class);
            balance += withdraw.getAmount();
            Account accounts = new Account ();
            accounts.setBalance(balance);
            accountService.updateAccount(accounts, id);
        }
        withdraw.setId(withdrawId);
        return withdraw;
    }

    public boolean existsById(Long id) {
        List<Withdraw> d= jdbcTemplate.query("SELECT * FROM Withdraw WHERE WithdrawId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Withdraw.class)));
        return !d.isEmpty();
    }
}
