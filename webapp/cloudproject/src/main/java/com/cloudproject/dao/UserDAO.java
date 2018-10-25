package com.cloudproject.dao;

import com.cloudproject.bean.Transaction;
import com.cloudproject.bean.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserDAO extends CrudRepository<User, UUID> {

    public User getUserByUsername(String username);

}
