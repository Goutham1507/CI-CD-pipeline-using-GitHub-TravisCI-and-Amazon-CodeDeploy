package com.cloudproject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "management.server.port=0", "management.context-path=/admin"}, classes = CloudprojectApplication.class)
@DirtiesContext

public class ApplicationJUnitTest {

        @LocalServerPort
        private int port;

        @Test
        public void testUserRegisterSuccess() throws JSONException {
            RequestSpecification request = RestAssured.given();
            JSONObject requestParams = new JSONObject();
            requestParams.put("username", "singh.har@husky.neu.edu");
            requestParams.put("password", "abc123");
            // Add a header stating the Request body is a JSON
            request.header("Content-Type", "application/json");

            // Add the Json to the body of the request
            request.body(requestParams.toString());

            request.port(port);
            // Post the request and check the response
            Response response = request.post("/user/register");

            int statusCode = response.getStatusCode();
            Assert.assertEquals("Account already exists!!", response.body().asString());
            Assert.assertEquals(statusCode, 200);

        }



}