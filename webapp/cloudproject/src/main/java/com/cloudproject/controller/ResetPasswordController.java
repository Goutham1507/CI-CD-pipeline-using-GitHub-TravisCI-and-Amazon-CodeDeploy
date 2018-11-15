package com.cloudproject.controller;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.cloudproject.bean.Message;
import com.cloudproject.bean.MetricsBean;
import com.timgroup.statsd.StatsDClient;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetPasswordController {

    @Value("${sns.topic.arn}")
    private String topicArn;

    @Autowired
    private StatsDClient metric;

    @Autowired
    @Bean
    public AmazonSNS getAmazonSNS() {
        AWSCredentialsProviderChain providerChain = new AWSCredentialsProviderChain(
                InstanceProfileCredentialsProvider.getInstance(),
                new ProfileCredentialsProvider()
        );
        return AmazonSNSClientBuilder.standard().withCredentials(providerChain).build();
    }

    @GetMapping("/reset")
    public Message resetPassword(Authentication authentication) {
        metric.incrementCounter("endpoint.reset.http.get");
        AmazonSNS amazonSNS = getAmazonSNS();

        String username = authentication.getName();
        System.out.println("Username:" + username);
        PublishRequest publishRequest = new PublishRequest(topicArn, username);
        PublishResult publishResult = amazonSNS.publish(publishRequest);

        System.out.println(publishResult.getMessageId());

        return null;
    }

}
