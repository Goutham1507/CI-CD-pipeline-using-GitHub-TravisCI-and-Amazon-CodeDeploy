package com.cloudproject;

import org.junit.Before;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class RegisterUserTest extends CloudprojectApplicationTests {

    private MockMvc mockMvc;
    
    @Before
    public void setup(){
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }



}
