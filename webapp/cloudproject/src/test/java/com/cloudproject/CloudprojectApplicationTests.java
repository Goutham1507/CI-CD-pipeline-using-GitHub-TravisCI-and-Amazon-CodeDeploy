package com.cloudproject;

import com.cloudproject.dao.DAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudprojectApplicationTests {


    private MockMvc mockMvc;

    @MockBean
    DAO dao;

    @Test
    public void contextLoads() {
    }

}
