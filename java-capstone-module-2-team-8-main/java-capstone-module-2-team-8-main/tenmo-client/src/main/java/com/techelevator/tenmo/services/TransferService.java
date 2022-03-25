package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.beans.AppletInitializer;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Scanner;

public class TransferService {

    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public TransferService(String url, AuthenticatedUser currentUser){
        this.currentUser = currentUser;
        this.API_BASE_URL = url;
    }

    public void sendBucks(){
        User[] users = null;
        Transfer transfer = new Transfer();
        try{
            Scanner scanner = new Scanner(System.in);
            users = restTemplate.exchange(API_BASE_URL+"listusers", HttpMethod.GET,makeAuthEntity(),User[].class).getBody();
            System.out.println("Users");
            System.out.println("----------------------------");
            for(User i : users){
                if(!i.getUsername().equals(currentUser.getUser().getUsername())){
                    System.out.println(i.getId() + "\t" + i.getUsername());
                }
            }
            System.out.println("----------------------------");
            System.out.print("Please Pick an ID to send money: ");
            transfer.setAccountFrom(currentUser.getUser().getId());

            transfer.setAccountTo(Long.parseLong(scanner.nextLine()));

            if (transfer.getAccountTo().equals(transfer.getAccountFrom())){
                System.out.println("Must send to someone BESIDES YOURSELF!");
                return;
            }
            System.out.print("Enter amount to send: ");
            BigDecimal sendAmount = scanner.nextBigDecimal();
            BigDecimal one = BigDecimal.valueOf(1);
            int res;
            res = sendAmount.compareTo(one);
            if (res == -1 ){
                System.out.println("Must send positive amount");
                return;
            }
            try {
                transfer.setAmount(sendAmount);
            }catch(NumberFormatException e){
                System.out.println("Error Entering Amount");
            }
            String output = restTemplate.exchange(API_BASE_URL+"transfer",HttpMethod.POST,makeTransferEntity(transfer),String.class).getBody();
            System.out.println(output);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Input Error"+ e.getMessage());
        }
    }

    public void requestBucks(){
        User[] users = null;
        Transfer transfer = new Transfer();
        try{
            Scanner scanner = new Scanner(System.in);
            users = restTemplate.exchange(API_BASE_URL+"listusers", HttpMethod.GET,makeAuthEntity(),User[].class).getBody();
            System.out.println("Users");
            System.out.println("----------------------------");
            for(User i : users){
                if(i.getId() != currentUser.getUser().getId()){
                    System.out.println(i.getId() + "\t" + i.getUsername());
                }
            }
            System.out.println("----------------------------");
            System.out.print("Please Pick an ID to request money: ");
            transfer.setAccountFrom(currentUser.getUser().getId());
            transfer.setAccountTo(Long.parseLong(scanner.nextLine()));
            System.out.print("Enter amount to request: ");
            try{
                transfer.setAmount(new BigDecimal(Double.parseDouble(scanner.nextLine())));
            }catch(NumberFormatException e){
                System.out.println("Error Entering Amount");
            }
            String output = restTemplate.exchange(API_BASE_URL+"request",HttpMethod.POST,makeTransferEntity(transfer),String.class).getBody();
            System.out.println(output);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Input Error" + e.getMessage());
        }
    }

    public void getUserTransfers(){
        Transfer[] transfers = null;
        Scanner scanner = new Scanner(System.in);
        try {
            transfers = restTemplate.exchange(API_BASE_URL + "account/transfer/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
            System.out.println("Transfers");
            System.out.println("TransID" + "\t " + "To/From" + "\t " + "Amount");
            System.out.println("----------------------------");
            for(int i=0;i<transfers.length;i++){
                if(transfers[i].getTransferTypeId() == 2) {
                    System.out.println(transfers[i].getTransferId() + "\t" + "To: " + transfers[i].getAccountTo() + "\t" + "$" + transfers[i].getAmount());
                }else{
                    System.out.println(transfers[i].getTransferId() + "\t" + "From: " + transfers[i].getAccountFrom() + "\t" + "$" + transfers[i].getAmount());
                }
            }
            System.out.println("----------------------------");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("Enter TransID for more details");
        Long transIdForDetail = Long.parseLong(scanner.nextLine());
        Transfer transfer = getTransferById(transIdForDetail);
        System.out.println();
        System.out.println("TransID" + "\t " + "To/From" + "\t " + "Amount");
        System.out.println("----------------------------");
        if(transfer.getTransferTypeId() == 2) {
            System.out.println(transfer.getTransferId() + "\t" + "To: " + transfer.getAccountTo() + "\t" + "$" + transfer.getAmount());
        }else{
            System.out.println(transfer.getTransferId() + "\t" + "From: " + transfer.getAccountFrom() + "\t" + "$" + transfer.getAmount());
        }
    }

    public Transfer getTransferById(Long transferId){
        Transfer transfer = null;
        try{
            transfer = restTemplate.exchange(API_BASE_URL + "transfer/"+transferId, HttpMethod.GET,makeAuthEntity(),Transfer.class).getBody();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return transfer;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

}
