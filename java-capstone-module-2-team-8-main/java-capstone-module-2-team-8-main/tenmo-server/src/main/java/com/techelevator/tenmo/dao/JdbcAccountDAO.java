package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDAO implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(Long id) {
        try {
            String sql = "SELECT balance FROM account WHERE user_id = ?";
            BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
            return balance;
        }catch(EmptyResultDataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public BigDecimal getBalanceByAccountId(Long id) {
        try {
            String sql = "SELECT balance FROM account WHERE account_id = ?";
            BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
            return balance;
        }catch(EmptyResultDataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public BigDecimal addToBalance(BigDecimal amount, Long id) {
        try{
            String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
            jdbcTemplate.update(sql,amount,id);
            Account account = findAccountById(id);
            return account.getBalance();
        }catch(EmptyResultDataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public BigDecimal subtractFromBalance(BigDecimal amount, Long id) {
        try{
            String sql = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
            jdbcTemplate.update(sql,amount,id);
            Account account = findAccountById(id);
            return account.getBalance();
        }catch(EmptyResultDataAccessException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Account findUserById(Long id) {
        Account account = null;
        String sql = "SELECT * From account WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,id);
        if(rowSet.next()){
            account = mapRowToAccount(rowSet);
        }
        return account;
    }

    @Override
    public Account findAccountById(Long id) {
        Account account = null;
        String sql = "SELECT * From account WHERE account_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,id);
        if(rowSet.next()){
            account = mapRowToAccount(rowSet);
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setBalance(result.getBigDecimal("balance"));
        account.setAccountId(result.getLong("account_id"));
        account.setUserId(result.getLong("user_id"));
        return account;
    }
}
