package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    String sendBucks(Long fromId, Long toId, BigDecimal amount);
    String requestBucks(Long fromId, Long toId, BigDecimal amount);
    List<Transfer> getAllTransfers(Long userId);
    Transfer getTransferById(Long transferId);
    List<Transfer> getPendingRequests(Long userId);
    String updateRequest(Transfer transfer, Long statusId);

}
