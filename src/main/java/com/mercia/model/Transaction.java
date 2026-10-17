package com.mercia.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Transactions")
public class Transaction {

	@Id
	private String transactionId;
	
	private String accountNumber;
	private LocalDate transactionDate;
	private Double amount;
	private String currency;
	private String transactionType;
	private String description;
	
	
}
