package com.cloudproject.dao;

import com.cloudproject.bean.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Repository
public interface TransactionDAO extends CrudRepository<Transaction, UUID> {


    public List<Transaction> findByUsername(String username);

    public Optional<Transaction> findById(UUID id);

    public void deleteById(UUID id);


}
