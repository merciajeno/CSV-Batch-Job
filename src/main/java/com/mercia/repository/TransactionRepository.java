package com.mercia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mercia.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>{

}
