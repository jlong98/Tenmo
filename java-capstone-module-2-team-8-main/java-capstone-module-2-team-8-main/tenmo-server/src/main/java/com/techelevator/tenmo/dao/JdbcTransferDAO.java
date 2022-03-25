package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDAO(JdbcTemplate jdbcTemplate, AccountDao accountDao){
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }

    @Override
    public String sendBucks(Long fromId, Long toId, BigDecimal amount) {
        Account fromAccount = accountDao.findUserById(fromId);
        Account toAccount = accountDao.findUserById(toId);
            if(amount.compareTo(accountDao.getBalanceByAccountId(fromAccount.getAccountId())) <= 0) {
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?)";
            jdbcTemplate.update(sql, fromAccount.getAccountId(), toAccount.getAccountId(), amount);
            accountDao.addToBalance(amount, toAccount.getAccountId());
            accountDao.subtractFromBalance(amount, fromAccount.getAccountId());
            return "Transfer Complete";
        }
        return "Transfer Failed";
    }

    @Override
    public String requestBucks(Long fromId, Long toId, BigDecimal amount) {
        Account fromAccount = accountDao.findUserById(fromId);
        Account toAccount = accountDao.findUserById(toId);
        if(amount.compareTo(new BigDecimal(0)) == 1){
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (1,1,?,?,?)";
            jdbcTemplate.update(sql,fromAccount.getAccountId(),toAccount.getAccountId(),amount);
            accountDao.addToBalance(amount,fromAccount.getAccountId());
            accountDao.subtractFromBalance(amount, toAccount.getAccountId());
            return "Transfer Complete";
        }
        return "Transfer Failed";
    }

    @Override
    public List<Transfer> getAllTransfers(Long userId) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT t.*, u.username AS fromUser, uu.username AS toUser FROM transfer t " +
                "JOIN account a ON t.account_from = a.account_id " +
                "JOIN account b ON t.account_to = b.account_id " +
                "JOIN tenmo_user u ON a.user_id = u.user_id " +
                "JOIN tenmo_user uu ON b.user_id = uu.user_id " +
                "WHERE a.user_id = ? OR  b.user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while(rowSet.next()){
            Transfer transfer = mapRowToTransfer(rowSet);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public Transfer getTransferById(Long transferId) {
        Transfer transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        while(rowSet.next()){
            transfer = mapRowToTransfer(rowSet);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getPendingRequests(Long userId) {
        return null;
    }

    @Override
    public String updateRequest(Transfer transfer, Long statusId) {
        return null;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getLong("transfer_type_id"));
        transfer.setTransferStatusId(results.getLong("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_From"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        try {
            transfer.setUserFrom(results.getString("userFrom"));
            transfer.setUserTo(results.getString("userTo"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            transfer.setTransferType(results.getString("transfer_type_desc"));
            transfer.setTransferStatus(results.getString("transfer_status_desc"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return transfer;
    }

}
