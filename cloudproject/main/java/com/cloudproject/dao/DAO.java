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

    public User checkLogin(String username){
        try{
//        System.out.println("in login check");
        Connection con=getConn();
        String query="SELECT * FROM logintb l where l.username='"+username+"'";
        PreparedStatement pst = con.prepareStatement(query);
//        System.out.println(query);
        ResultSet rs = pst.executeQuery();
        if(rs.next() == false){
            pst.close();
            con.close();
            return null;
        }else{
            User u = new User(rs.getString("username").trim(), rs.getString("password").trim());
            pst.close();
            con.close();
            return u;
        }

    }catch (Exception e){

    }
        return null;
    }

    public static boolean checkUser(String username){
        try{
//            System.out.println("in check");
            Connection con=getConn();
        String query="SELECT count(*) as total FROM logintb l where l.username='"+username+"'";
        PreparedStatement pst = con.prepareStatement(query);
//        System.out.println(query);
        ResultSet rs = pst.executeQuery();
        rs.next();
        int count=rs.getInt("total");
        pst.close();
        con.close();

            if(count==0) {
               return false;
           }
           else
               return true;

    }catch (Exception e){

        }
        return false;

    }

    public static int createUser(String username,String password){
        int affectedRows = 0;
        try{
            Connection con=getConn();
            String query="INSERT INTO logintb(username,password) VALUES(?,?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username.trim());
            pst.setString(2, password.trim());
            affectedRows = pst.executeUpdate();
//            System.out.println("AR: "+affectedRows);
//            System.out.println("affected: "+affectedRows);
            pst.close();
            con.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return affectedRows;
    }
}
