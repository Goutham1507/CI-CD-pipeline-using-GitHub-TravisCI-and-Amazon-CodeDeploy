package com.cloudproject.dao;

import com.cloudproject.bean.User;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class UserDAO extends DAO {

    public User checkLogin(String username){
        try{
//        System.out.println("in login check");
            Connection con=getConn();
            String query="SELECT * FROM usertbl l where l.username='"+username+"'";
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
            String query="SELECT count(*) as total FROM usertbl l where l.username='"+username+"'";
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
            String query="INSERT INTO usertbl(username,password) VALUES(?,?)";
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
