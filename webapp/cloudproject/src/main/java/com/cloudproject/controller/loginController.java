package com.cloudproject.controller;

import com.cloudproject.dao.DAO;
import com.cloudproject.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

@RestController
public class loginController {

    @Autowired
    BCryptPasswordEncoder bCry;

    @Autowired
    UserDAO dao;


    @RequestMapping(value = "/time", method = RequestMethod.GET)
    public Map<String, String> login(HttpServletRequest request) throws UnsupportedEncodingException {

        System.out.print("----------------------------------------"+System.getenv("SPRING_DATASOURCE_URL")+"-----------");
        String message;
        String authType=request.getHeader("Authorization");
        Map<String, String> json = new HashMap<>();
        System.out.println(authType);

        if(authType!=null && authType.contains("Basic")) {
            message= new Date().toString() + ". You are logged in!";
        }
        else{
            message="You are not logged in!!";
        }
        json.put("message",message);

        return json;

    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public Map<String, String> register(HttpServletRequest request) throws UnsupportedEncodingException {

        String message;

        String userName = request.getParameter("username");
        String password = request.getParameter("password");


        String passEncrypted = bCry.encode(password);
        Map<String, String> json = new HashMap();

        boolean flag = dao.checkUser(userName);

        if (!Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", userName)) {
            message = "Please enter username in proper Email Format!";
        } else {

            if (flag) {
                message = "User already exists";
            } else {
                if (dao.createUser(userName, passEncrypted) == 1) {
                    message = "User registered successfully";
                } else {
                    message = "User registration Failed";
                }
            }
        }
        json.put("user", message);

        return json;
    }
}