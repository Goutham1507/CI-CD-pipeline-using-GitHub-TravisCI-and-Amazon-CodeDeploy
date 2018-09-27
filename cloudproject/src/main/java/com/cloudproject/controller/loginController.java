package com.cloudproject.controller;

import com.cloudproject.dao.DAO;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

@RestController
public class loginController {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @RequestMapping(value = "/time", method = RequestMethod.GET)
    public Map<String, String> login(HttpServletRequest request) throws UnsupportedEncodingException {
        String message;
        String authType=request.getHeader("Authorization");
        System.out.println(authType);
        StringTokenizer tokenizer = new StringTokenizer(authType," ");
        tokenizer.nextToken();
        String cred = tokenizer.nextToken();

        byte[] asBytes = Base64.getDecoder().decode(cred);
        cred = new String(asBytes, "utf-8");
        Map<String, String> json = new HashMap<>();
        if(authType!=null && authType.contains("Basic")) {
            message= new Date().toString() + ". You are logged in!";
        }
        else{
            message="User not logged in!!";
        }
        json.put("date",message);

        return json;

    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public Map<String, String> register(HttpServletRequest request) throws UnsupportedEncodingException {

        String message=null;

        String authType=request.getHeader("Authorization");
        StringTokenizer tokenizer = new StringTokenizer(authType," ");
        tokenizer.nextToken();
        String cred = tokenizer.nextToken();
        byte[] asBytes = Base64.getDecoder().decode(cred);
        cred = new String(asBytes, "utf-8");
        tokenizer = new StringTokenizer(cred,":");
        String userName = tokenizer.nextToken();
        String password = tokenizer.nextToken();
        password = bCryptPasswordEncoder().encode(password);
            Map<String, String> json = new HashMap();
            DAO dao = new DAO();
            boolean flag = dao.checkUser(userName);

        if(!Pattern.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", userName)){
            message = "Please enter username in proper Email Format!";
        }else {

            if (authType != null && authType.contains("Basic")) {
                if (flag) {
                    message = "User already exists";
                } else {
                    if (DAO.createUser(userName, password) == 1) {
                        message = "User registered successfully";
                    } else {
                        message = "User registration Failed";
                    }
                }
            } else {
                message = "User not logged in!!";
            }
        }
        json.put("user", message);

        return json;
    }

//    public static String hashPassword(String password_plaintext) {
//        int workload = 12;
//        String salt = BCrypt.gensalt(workload);
//        String hashed_password = BCrypt.hashpw(password_plaintext, salt);
//        return(hashed_password);
//    }
}