package com.cloudproject;

import com.cloudproject.controller.loginController;
import com.cloudproject.dao.DAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(value = loginController.class, secure = false)
public class CloudprojectApplicationTests {


    private MockMvc mockMvc;

    @MockBean
    DAO dao;

    @Test
    public void contextLoads() {
    }

    @Test
    public void registerUserTest() throws Exception {
        String username = "hiren";
        DAO dao = new DAO();
        Assert.assertTrue(dao.checkUser(username));
    }

}
