package com.cloudproject.repository;


import com.cloudproject.bean.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction,Long> {
    List<Transaction> findByUsername(String username);

    Transaction save(Transaction transaction);
}
