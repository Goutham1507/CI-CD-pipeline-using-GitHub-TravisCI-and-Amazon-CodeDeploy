package com.cloudproject.controller;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.cloudproject.bean.Attachment;
import com.cloudproject.bean.Message;
import com.cloudproject.bean.MetricsBean;
import com.cloudproject.bean.Transaction;
import com.cloudproject.dao.TransactionDAO;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.UUID;

@RestController
public class TransactionController {

    @Autowired
    TransactionDAO transactionDAO;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Autowired
    private StatsDClient metric;

    @Value("${amazonProperties.endpointUrl}")
    private String endPointUrl;

    @Autowired
    @Bean
    public AmazonS3 getAmazonS3Client() {
        AWSCredentialsProviderChain providerChain = new AWSCredentialsProviderChain(
                InstanceProfileCredentialsProvider.getInstance(),
                new ProfileCredentialsProvider()
        );
        return AmazonS3ClientBuilder.standard()
                .withCredentials(providerChain)
                .build();
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    public ArrayList<Transaction> getTransactions(HttpServletResponse response, Authentication authentication){
        metric.incrementCounter("endpoint.test.http.get");
        String username = authentication.getName();
        //ArrayList<Transaction> transactions = transactionDAO.getTransactions(username);
        ArrayList<Transaction> transactions = (ArrayList<Transaction>) transactionDAO.findByUsername(username);
        if(transactions == null){
            System.out.println("No Transactions found!!");
        }
        return  transactions;
    }


    @RequestMapping(value = "/transaction", method = RequestMethod.POST, produces = "application/json")
    public Object createTransactions(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        metric.incrementCounter("endpoint.test.http.post");
        String description = request.getParameter("description");
        String merchant = request.getParameter("merchant");
        String amount = request.getParameter("amount");
        String date = request.getParameter("date");
        String category = request.getParameter("category");
        String username = authentication.getName().trim();

        if(description == null || merchant == null || amount == null || date == null || category == null){
            return new Message("Please enter all the details for Description, Merchant, Amount, Date and Category.");
        }


        if(description.equals("") || merchant.equals("") || amount.equals("") || date.equals("") || category.equals("")){
            return new Message("Please enter all the details for Description, Merchant, Amount, Date and Category.");
        }
        
        if (!(date.matches("^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/((19|2[0-9])[0-9]{2})$"))){
            return new Message("Please enter date in valid format MM/DD/YYYY");
        }

        if(!(amount.matches("[0-9]+([,.][0-9]{1,2})?"))){
            return new Message("Please enter amount in valid format!");
        }

        Transaction transaction = new Transaction();
        transaction.setMerchant(merchant);
        transaction.setDate(date);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setUsername(username.trim());
        transaction.setDescription(description);
        transactionDAO.save(transaction);

        return transaction;

    }

    @RequestMapping(value = "/transaction/*", method = RequestMethod.PUT, produces = "application/json")
    public Object putTransactions(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        metric.incrementCounter("endpoint.test.http.put");
        StringTokenizer tokenizer = new StringTokenizer(request.getRequestURI(),"/");
        tokenizer.nextToken();
        UUID id = null;
        try {
            id = UUID.fromString(tokenizer.nextToken());
        }catch (Exception e){
            return new Message("Please enter a valid ID of the Transaction!");
        }

        Transaction transaction = null;

        String description = request.getParameter("description");
        String merchant = request.getParameter("merchant");
        String amount = request.getParameter("amount");
        String date = request.getParameter("date");
        String category = request.getParameter("category");


        if(description == null || merchant == null || amount == null || date == null || category == null){
            return new Message("Please enter all the details for Description, Merchant, Amount, Date and Category.");
        }

        if(description.equals("") || merchant.equals("") || amount.equals("") || date.equals("") || category.equals("")){
            return new Message("Please enter all the details for Description, Merchant, Amount, Date and Category.");
        }


        try {
            transaction = (transactionDAO.findById(id)).get();
        }catch(NoSuchElementException e){
            return new Message("No such transaction exists!");
        }

        if (!(date.matches("^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/((19|2[0-9])[0-9]{2})$"))){
            return new Message("Please enter date in valid format MM/DD/YYYY");
        }

        if(!(amount.matches("^(\\d*\\.?\\d{0,2})$"))){
            return new Message("Please enter amount in valid format!");
        }
        String username = auth.getName().trim();
        if((transaction.getUsername().trim()).equals(auth.getName().trim())) {
            transaction.setDescription(request.getParameter("description"));
            transaction.setUsername(username);
            transaction.setAmount(amount);
            transaction.setCategory(request.getParameter("category"));
            transaction.setDate(date);
            transaction.setMerchant(request.getParameter("merchant"));
            transactionDAO.save(transaction);
        }else{
            System.out.println("Transaction not authorized!");
            return new Message("You are not Authorized for this Transaction!");
        }
        return transaction;
    }

    @RequestMapping(value = "/transaction/*", method = RequestMethod.DELETE, produces = "application/json")
    public Message deleteTransaction(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        metric.incrementCounter("endpoint.test.http.delete");
        StringTokenizer tokenizer = new StringTokenizer(request.getRequestURI(),"/");
        tokenizer.nextToken();

        UUID id = null;
        try{
            id = UUID.fromString(tokenizer.nextToken());
        }catch (Exception e){
            return new Message("Please enter a valid ID of the Transaction!");
        }
        Transaction transaction = (transactionDAO.findById(id)).get();

        if((transaction.getUsername().trim()).equals(auth.getName().trim())) {
            try {
                AmazonS3 s3client = getAmazonS3Client();
                for(Attachment a : transaction.getAttachments()){
                    String url = a.getUrl().trim();
                    String fileName = url.substring(url.lastIndexOf('/') + 1, url.length()).trim();
                    s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
                }
                transactionDAO.deleteById(id);
            }catch(EmptyResultDataAccessException e){
                return new Message("Transaction does not exist!");
            }
            return new Message("Transaction deleted Successfully!");
        }else{
            return new Message("You are not Authorized for this Transaction!");
        }
    }
}


