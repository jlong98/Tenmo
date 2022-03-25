package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private String API_BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url, AuthenticatedUser currentUser){
        this.API_BASE_URL = url;
        this.currentUser = currentUser;
    }

    public void getBalance(){
        BigDecimal balance;
        try{
            balance = restTemplate.exchange(API_BASE_URL+"balance/" + currentUser.getUser().getId(), HttpMethod.GET,makeAuthEntity(),BigDecimal.class).getBody();
            System.out.println("Current Balance is: $"+balance);
        }catch(RestClientException e){
            System.out.println(e.getMessage());
        }
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

}
