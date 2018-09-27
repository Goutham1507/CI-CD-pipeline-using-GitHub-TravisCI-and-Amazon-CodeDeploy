package com.cloudproject;

import com.cloudproject.controller.loginController;
import com.cloudproject.dao.DAO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "management.server.port=0", "management.context-path=/admin"}, classes = CloudprojectApplication.class)
public class CloudprojectApplicationTests {


    @LocalServerPort
    private int port;

    @Test
    public void testUserRegisterSuccess() throws JSONException {
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "singh.har@husky.neu.edu");
        requestParams.put("password", "ABC123");
        // Add a header stating the Request body is a JSON
        request.header("Content-Type", "application/json");

        // Add the Json to the body of the request
        request.body(requestParams.toString());

        request.port(port);
        // Post the request and check the response
        Response response = request.post("/user/register");

        int statusCode = response.getStatusCode();
        Assert.assertEquals("{\"message\":\" You are not registered!\"}\n", response.body().asString());
        Assert.assertEquals(statusCode,401);

    }

}
