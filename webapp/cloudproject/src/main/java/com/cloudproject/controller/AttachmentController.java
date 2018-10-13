package com.cloudproject.controller;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cloudproject.bean.Attachment;
import com.cloudproject.bean.Message;
import com.cloudproject.bean.Transaction;
import com.cloudproject.dao.AttachmentDAO;
import com.cloudproject.dao.DAO;
import com.cloudproject.dao.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@PropertySource("classpath:application.properties")
public class AttachmentController {

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    TransactionDAO transactionDAO;
    private String profile=System.getProperty("spring.profiles.active");

    private String bucketName, endPointUrl;

    @Autowired
    Properties properties;

    @Autowired
    @Bean
    public AmazonS3 getAmazonS3Client(){
        AWSCredentialsProviderChain providerChain = new AWSCredentialsProviderChain(
                InstanceProfileCredentialsProvider.getInstance(),
                new ProfileCredentialsProvider()
        );
        return AmazonS3ClientBuilder.standard()
                .withCredentials(providerChain)
                .build();
    }

    @Bean
    public String getBucketProperties(){
        if("dev".equals(profile)) {
            Path myPath = Paths.get("src/main/resources/application-dev.properties");

            try {
                BufferedReader bf = Files.newBufferedReader(myPath,
                        StandardCharsets.UTF_8);

                properties.load(bf);
            } catch (IOException ex) {
                Logger.getLogger(DAO.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            this.bucketName = properties.getProperty("amazonProperties.bucketName");
            System.out.println(bucketName);
            this.endPointUrl = properties.getProperty("amazonProperties.endpointUrl");
        }else {
            this.bucketName = null;
            this.endPointUrl = null;
        }
        return null;
    }


    @RequestMapping(value = "/transaction/{id}/attachments", method = RequestMethod.GET, produces = "application/json")
    public ArrayList<Attachment> getAttachment(HttpServletResponse response, Authentication authentication, @PathVariable(value="id") String id){
        String username = authentication.getName();

        ArrayList<Attachment> attachments = attachmentDAO.findByTransactionId(UUID.fromString(id));
        return  attachments;
    }


    @RequestMapping(value = "/transaction/{id}/attachments", method = RequestMethod.POST, produces = "application/json")
    public Object createAttachment(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @PathVariable(value="id") String id) {
        Transaction transaction;
        String transactionId=id;
        String url = request.getParameter("url");
        String fileUrl="";
        if(url.equals(""))
        {
            return new Message("Url cannot be blank!");
        }
        File file = new File(url);
        String ext= file.getName().substring(file.getName().lastIndexOf("."));
        String fileName=transactionId + "_" + new Date().getTime()+ext;


        if(!(ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".jpg"))){
            return new Message("Unsupported extension! Only .jpg, .jpeg, .png file allowed");
        }

          Attachment attachment=new Attachment(url, UUID.fromString(transactionId));
        try {
            transaction = (transactionDAO.findById(UUID.fromString(transactionId))).get();
        }catch(NoSuchElementException e){
            return new Message("No such transaction exists!");
        }

          attachment.setTransaction(transaction);

/////////////////////code for s3 upload////////////////////////////////////////////
        if("dev".equalsIgnoreCase(profile))
        {
            getBucketProperties();

            AmazonS3 s3client = getAmazonS3Client();

            s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
            fileUrl = endPointUrl + "/" + bucketName + "/" + fileName;

        }
        /////////////////////code for s3////////////////////////////////////////////

        else{
            fileUrl="src/images/"+fileName;
            try {
                Files.copy(new File(url).toPath(),new File(fileUrl).toPath());
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (IOException e) {
                System.out.println("IO ");
            }

        }
        attachment.setUrl(fileUrl);

        attachmentDAO.save(attachment);
        response.setStatus(HttpServletResponse.SC_OK);

        return attachment;

    }



    @RequestMapping(value = "/transaction/{transId}/attachments/{attachID}", method = RequestMethod.DELETE, produces = "application/json")
    public Message deleteAttachments(HttpServletRequest request, HttpServletResponse response, Authentication auth, @PathVariable(value="transId") String transId, @PathVariable(value="attachID") String attachID) {


        UUID getAttachID=null;
        String fileName;
        Attachment attachment;
        String url="";
        try{

            getAttachID = UUID.fromString(attachID);
            attachment = (attachmentDAO.findById(getAttachID)).get();
            url=attachment.getUrl().trim();
             fileName= url.substring( url.lastIndexOf('/')+1, url.length() ).trim();
        }catch (Exception e){
            return new Message("Please enter a valid ID of the Attachment!");
        }

        if((attachment.getTransaction().getUsername().trim()).equals(auth.getName().trim())) {
            try {
                if("dev".equalsIgnoreCase(profile)){
                    getBucketProperties();


                    AmazonS3 s3client = getAmazonS3Client();

                    System.out.println(fileName);
                    s3client.deleteObject(new DeleteObjectRequest(bucketName,fileName));
                }else{
                    File file = new File(url);
                    System.out.println(url);
                    if(!file.delete()){
                        System.out.println("File not found!");
                    }
                }
                attachmentDAO.deleteById(getAttachID);
            }catch(EmptyResultDataAccessException e){
                return new Message("Transaction does not exist!");
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return new Message("Transaction deleted Successfully!");
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new Message("You are not Authorized for this Transaction!");
        }
    }



    @RequestMapping(value = "/transaction/{transId}/attachments/{attachID}", method = RequestMethod.PUT, produces = "application/json")
    public Message updateAttachments(HttpServletRequest request, HttpServletResponse response, Authentication auth, @PathVariable(value="transId") String transId, @PathVariable(value="attachID") String attachID) {


        UUID getAttachID=null;
        String fileName;
        Attachment attachment;
        String url="";

        String newUrl = request.getParameter("url");

        if(url.equals(""))
        {
            return new Message("Url cannot be blank!");
        }

        File newFile = new File(newUrl);
        String ext= newFile.getName().substring(newFile.getName().lastIndexOf("."));

        if(!(ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".jpg"))){
            return new Message("Unsupported extension! Only .jpg, .jpeg, .png file allowed");
        }

        try{
            getAttachID = UUID.fromString(attachID);
            attachment = (attachmentDAO.findById(getAttachID)).get();
            url=attachment.getUrl().trim();
            fileName= url.substring( url.lastIndexOf('/')+1, url.length() ).trim();
        }catch (Exception e){
            return new Message("Please enter a valid ID of the Attachment!");
        }

        if((attachment.getTransaction().getUsername().trim()).equals(auth.getName().trim())) {

            if("dev".equalsIgnoreCase(profile)){
                getBucketProperties();
                AmazonS3 s3client = getAmazonS3Client();
                s3client.putObject(new PutObjectRequest(bucketName, fileName, newFile));
            }else{
                File file = new File(url);
                System.out.println(url);
                if(!file.delete()){
                    System.out.println("File not found!");
                }
                try {
                    Files.copy(new File(newUrl).toPath(),new File(url).toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new Message("You are not Authorized for this Transaction!");
        }
        return new Message("Transaction updated");
    }
}