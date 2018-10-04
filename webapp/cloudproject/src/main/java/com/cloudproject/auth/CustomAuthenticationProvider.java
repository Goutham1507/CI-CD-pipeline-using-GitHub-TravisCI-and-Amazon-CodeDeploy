package com.cloudproject.auth;

import com.cloudproject.bean.User;
import com.cloudproject.dao.DAO;
import com.cloudproject.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    UserDAO dao;

    @Autowired
    private BCryptPasswordEncoderBean bCry;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user = dao.checkLogin(name);
        if(user == null){
            return null;
        }

        System.out.println(bCry.bCryptPasswordEncoder().encode(password));

        System.out.println(user.getPassword());
        boolean match = bCry.bCryptPasswordEncoder().matches(password,user.getPassword());
        if(match){
            return new UsernamePasswordAuthenticationToken(name,password,new ArrayDeque<>());
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public static String hashPassword(String password_plaintext) {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);
        return(hashed_password);
    }
}