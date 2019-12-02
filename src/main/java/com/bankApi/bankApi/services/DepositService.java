package com.bankApi.bankApi.services;

import com.bankApi.bankApi.models.*;
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
public class DepositService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DepositService(JdbcTemplate jdbcTemplate) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate must not be null.");
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Deposit> findAllByAccountId(Long id){
        List<Long> depositIds = jdbcTemplate.queryForList("SELECT DepositId FROM Deposit WHERE AccountId = ?", new Object[] {id}, Long.class);
        List<Deposit> deposits = new ArrayList<>();
        depositIds.forEach(v -> {
            Deposit deposit = this.findById(v);
            deposit.setId(v);
            deposits.add(deposit);
        });
        return deposits;
    }

    public Deposit findById(Long id) {
        Deposit deposit = jdbcTemplate.query("SELECT * FROM Deposit WHERE DepositId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Deposit.class))).get(0);
        Timestamp time = jdbcTemplate.queryForObject("SELECT date FROM Deposit WHERE DepositId = ?", new Object[]{id}, Timestamp.class);
        deposit.setTransaction_date(String.format("%1$TD %1$TT", time));
        return deposit;
    }

    public Deposit updateDeposit(Deposit deposit, long id) {
        if (deposit.getType() != null) {
            jdbcTemplate.update("UPDATE Deposit SET type = ? WHERE DepositId = ?", deposit.getType(), id);
        }
        if (deposit.getAmount() != null) {
            String currentStatus = jdbcTemplate.queryForObject("SELECT status FROM Deposit WHERE DepositId = ?", new Object[]{id}, String.class);
            String currentMedium = jdbcTemplate.queryForObject("SELECT Medium FROM Deposit WHERE DepositId = ?", new Object[]{id}, String.class);
            if (currentStatus != null && currentStatus.equals(TransactionStatus.Completed.toString()) && currentMedium != null && currentMedium.equals(TransactionMedium.Balance.toString())){
                Long accountId = jdbcTemplate.queryForObject("SELECT AccountId FROM Deposit WHERE DepositId = ?", new Object[] {id}, Long.class);
                Double currentAmount = jdbcTemplate.queryForObject("Select Amount FROM Deposit WHERE DepositId = ?", new Object[] {id}, Double.class);
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = ?", new Object[] {accountId}, Double.class);
                if (currentAmount == null) currentAmount = 0.0;
                double amountToChange = deposit.getAmount() - currentAmount;
                balance += amountToChange;
                jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)",balance, id);
            }
            jdbcTemplate.update("UPDATE Deposit SET amount = ? WHERE DepositId = ?", deposit.getAmount(), id);
        }
        if (deposit.getStatus() != null) {
            String currentStatus = jdbcTemplate.queryForObject("SELECT status FROM Deposit WHERE DepositId = ?", new Object[]{id}, String.class);
            Double depositAmount = jdbcTemplate.queryForObject("SELECT Amount FROM Deposit WHERE DepositId = ?", new Object[]{id}, Double.class);
            if (deposit.getStatus() == TransactionStatus.Completed && currentStatus != null && !currentStatus.equals(TransactionStatus.Completed.toString())){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance += depositAmount;
                jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)",balance, id);
            } else if (deposit.getStatus() != TransactionStatus.Completed && currentStatus != null && currentStatus.equals(TransactionStatus.Completed.toString())){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance -= depositAmount;
                jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)",balance, id);
            }
            jdbcTemplate.update("UPDATE Deposit SET status = ? WHERE DepositId = ?", deposit.getStatus().toString(), id);
        }
        if (deposit.getMedium() != null) {
            Double depositAmount = jdbcTemplate.queryForObject("SELECT Amount FROM Deposit WHERE DepositId = ?", new Object[]{id}, Double.class);
            String currentMedium = jdbcTemplate.queryForObject("SELECT Medium FROM Deposit WHERE DepositId = ?", new Object[]{id}, String.class);
            if (deposit.getMedium() == TransactionMedium.Balance && currentMedium != null && !currentMedium.equals(TransactionMedium.Balance.toString())){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance += depositAmount;
                jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)",balance, id);
            } else if (deposit.getMedium() != TransactionMedium.Balance && currentMedium != null && currentMedium.equals(TransactionMedium.Balance.toString())){
                Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
                balance -= depositAmount;
                jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)",balance, id);
            }
            jdbcTemplate.update("UPDATE Deposit SET medium = ? WHERE DepositId = ?", deposit.getMedium().toString(), id);
        }
        if (deposit.getDescription() != null) {
            jdbcTemplate.update("UPDATE Deposit SET description = ? WHERE DepositId = ?", deposit.getDescription(), id);
        }
        return this.findById(id);
    }

    public void deleteById(Long id) {
        Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)", new Object[] {id}, Double.class);
        Double amount = jdbcTemplate.queryForObject("SELECT amount FROM Deposit WHERE DepositId = ?", new Object[]{id}, Double.class);
        balance -= amount;
        jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = (SELECT AccountId FROM Deposit WHERE DepositId = ?)",balance, id);
        jdbcTemplate.update("DELETE FROM deposit WHERE DepositId = ?", id);
    }

    public Deposit createDeposit(Deposit deposit, Long id) {
        Timestamp time = new Timestamp(new java.util.Date().getTime());
        deposit.setAccountId(id);
        jdbcTemplate.update("INSERT INTO deposit (type, status, medium, date, accountId, amount, description) VALUES (?, ?, ?, ?, ?,?,?)",
              deposit.getType().toString(),deposit.getStatus().toString(),deposit.getMedium().toString(),time,id,deposit.getAmount(),deposit.getDescription());
        Long depositId = jdbcTemplate.queryForObject("SELECT MAX(DepositId) FROM Deposit WHERE AccountId = ?", new Object[] {id}, Long.class);

        if(deposit.getMedium()== TransactionMedium.Balance && deposit.getStatus() == TransactionStatus.Completed){
            Double balance = jdbcTemplate.queryForObject("SELECT BALANCE from Account WHERE AccountId = ?", new Object[] {id}, Double.class);
            balance += deposit.getAmount();
            jdbcTemplate.update("UPDATE Account SET Balance = ? WHERE AccountId = ?",balance, id);
        }
        deposit.setId(depositId);
        return deposit;
    }

    public boolean existsById(Long id) {
        List<Deposit> d= jdbcTemplate.query("SELECT * FROM Deposit WHERE DepositId = ?", new Object[] {id}, (new BeanPropertyRowMapper<>(Deposit.class)));
        return !d.isEmpty();
    }
}
