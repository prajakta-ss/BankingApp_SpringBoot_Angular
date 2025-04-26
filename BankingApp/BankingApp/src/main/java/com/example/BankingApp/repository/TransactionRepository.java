package com.example.BankingApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.BankingApp.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
	List<Transaction> findByAccountId(long account_id);

}
