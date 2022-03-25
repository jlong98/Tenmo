package com.techelevator.tenmo.controller;

import java.util.List;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.*;
import java.math.BigDecimal;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;

    public TransferController(TransferDao transferDao){
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public String sendTransferRequest(@RequestBody Transfer transfer){
        String result = transferDao.sendBucks(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return result;
    }

    @RequestMapping(path = "request", method = RequestMethod.POST)
    public String requestTransferRequest(@RequestBody Transfer transfer){
        String result = transferDao.requestBucks(transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());
        return result;
    }

    @RequestMapping(value = "account/transfer/{id}", method = RequestMethod.GET)
    public List<Transfer> getUserTransfers(@PathVariable Long id){
        List<Transfer> list = transferDao.getAllTransfers(id);
        return list;
    }

    @RequestMapping(value = "transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable Long transferId){
        Transfer transfer = transferDao.getTransferById(transferId);
        return transfer;
    }

}


