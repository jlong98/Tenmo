package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    BigDecimal getBalance(Long id);
    BigDecimal getBalanceByAccountId(Long id);
    BigDecimal addToBalance(BigDecimal amount,Long id);
    BigDecimal subtractFromBalance(BigDecimal amount,Long id);
    Account findUserById(Long id);
    Account findAccountById(Long id);
}
