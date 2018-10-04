package com.cloudproject.dao;

import com.cloudproject.bean.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Component
public class DAO {

    public static Connection con;
    public static Connection getConn() throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();
        Path myPath = Paths.get("src/main/resources/application.properties");

        try {
            BufferedReader bf = Files.newBufferedReader(myPath,
                    StandardCharsets.UTF_8);

            props.load(bf);
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        String url = props.getProperty("spring.datasource.url");
        String user = props.getProperty("spring.datasource.username");
        String passwd = props.getProperty("spring.datasource.password");
        try {

             con = DriverManager.getConnection(url, user, passwd);

        } catch (Exception e) {

        }

        return con;
    }

}
