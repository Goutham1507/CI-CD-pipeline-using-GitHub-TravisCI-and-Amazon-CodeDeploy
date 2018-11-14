package com.cloudproject.controller;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cloudproject.bean.Attachment;
import com.cloudproject.bean.Message;
import com.cloudproject.bean.MetricsBean;
import com.cloudproject.bean.Transaction;
import com.cloudproject.dao.AttachmentDAO;
import com.cloudproject.dao.TransactionDAO;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@RestController
@PropertySource("classpath:application.properties")
public class AttachmentController {

    @Autowired
    private StatsDClient metric;

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    TransactionDAO transactionDAO;

    @Autowired
    Properties properties;

    private String profile = System.getProperty("spring.profiles.active");

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

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


    @RequestMapping(value = "/transaction/{id}/attachments", method = RequestMethod.GET, produces = "application/json")
    public ArrayList<Attachment> getAttachment(HttpServletResponse response, Authentication authentication, @PathVariable(value = "id") String id) {
        metric.incrementCounter("endpoint.test.http.get");

        String username = authentication.getName();

        ArrayList<Attachment> attachments = attachmentDAO.findByTransactionId(UUID.fromString(id));
        return attachments;
    }


    @RequestMapping(value = "/transaction/{id}/attachments", method = RequestMethod.POST, produces = "application/json")
    public Object createAttachment(HttpServletRequest request, @RequestParam("file") MultipartFile file, HttpServletResponse response, Authentication authentication, @PathVariable(value = "id") String id) throws IOException {
        metric.incrementCounter("endpoint.test.http.post");

        Transaction transaction;
        String transactionId = id;
        String fileUrl = "";
        String ext="";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());

        System.out.println("--------------->Filename: "+file.getOriginalFilename());

        ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = transactionId + "_" + new Date().getTime() + ext;


        if (!(ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".jpg"))) {
            return new Message("Unsupported extension! Only .jpg, .jpeg, .png file allowed");
        }

        Attachment attachment = new Attachment("",UUID.fromString(transactionId));
        try {
            transaction = (transactionDAO.findById(UUID.fromString(transactionId))).get();
        } catch (NoSuchElementException e) {
            return new Message("No such transaction exists!");
        }

        attachment.setTransaction(transaction);


/////////////////////code for s3 upload////////////////////////////////////////////

        AmazonS3 s3client = getAmazonS3Client();

        System.out.println("-------> BucketName:" + bucketName);
        System.out.println("-------> EndPointUrl:" + endPointUrl);
        System.out.println("-------> FileName:" + fileName);
        System.out.println("-------> file:" + file.getOriginalFilename());
//        System.out.println("-------> file1:" + file1.exists());


        s3client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(),objectMetadata));
        fileUrl = endPointUrl + "/" + bucketName + "/" + fileName;

        attachment.setUrl(fileUrl);

        attachmentDAO.save(attachment);
        response.setStatus(HttpServletResponse.SC_OK);

        return attachment;
    }


    @RequestMapping(value = "/transaction/{transId}/attachments/{attachID}", method = RequestMethod.DELETE, produces = "application/json")
    public Message deleteAttachments(HttpServletRequest request, HttpServletResponse response, Authentication auth, @PathVariable(value = "transId") String transId, @PathVariable(value = "attachID") String attachID) {

        metric.incrementCounter("endpoint.test.http.delete");

        UUID getAttachID = null;
        String fileName;
        Attachment attachment;
        String url = "";

        try {

            getAttachID = UUID.fromString(attachID);
            attachment = (attachmentDAO.findById(getAttachID)).get();
            url = attachment.getUrl().trim();
            fileName = url.substring(url.lastIndexOf('/') + 1, url.length()).trim();
        } catch (Exception e) {
            return new Message("Please enter a valid ID of the Attachment!");
        }

        if ((attachment.getTransaction().getUsername().trim()).equals(auth.getName().trim())) {
            try {

                AmazonS3 s3client = getAmazonS3Client();

                System.out.println(fileName);
                s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
                attachmentDAO.deleteById(getAttachID);
            } catch (EmptyResultDataAccessException e) {
                return new Message("Transaction does not exist!");
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return new Message("Transaction deleted Successfully!");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new Message("You are not Authorized for this Transaction!");
        }
    }


    @RequestMapping(value = "/transaction/{transId}/attachments/{attachID}", method = RequestMethod.PUT, produces = "application/json")
    public Message updateAttachments(HttpServletRequest request, @RequestParam("file") MultipartFile newFile, HttpServletResponse response, Authentication auth, @PathVariable(value = "transId") String transId, @PathVariable(value = "attachID") String attachID) throws IOException {
        metric.incrementCounter("endpoint.test.http.put");

        UUID getAttachID = null;
        String fileName;
        Attachment attachment;
        String url = "", fileUrl = "";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(newFile.getContentType());

        String ext = newFile.getOriginalFilename().substring(newFile.getOriginalFilename().lastIndexOf("."));

        if (!(ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".jpg"))) {
            return new Message("Unsupported extension! Only .jpg, .jpeg, .png file allowed");
        }

        try {
            getAttachID = UUID.fromString(attachID);
            attachment = (attachmentDAO.findById(getAttachID)).get();
            url = attachment.getUrl().trim();
            fileName = url.substring(url.lastIndexOf('/') + 1, url.length()).trim();
        } catch (Exception e) {
            return new Message("Please enter a valid ID of the Attachment!");
        }

        if ((attachment.getTransaction().getUsername().trim()).equals(auth.getName().trim())) {

            AmazonS3 s3client = getAmazonS3Client();
            s3client.putObject(new PutObjectRequest(bucketName, fileName,  newFile.getInputStream(),objectMetadata));

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new Message("You are not Authorized for this Transaction!");
        }
        return new Message("Transaction updated");
    }
}
