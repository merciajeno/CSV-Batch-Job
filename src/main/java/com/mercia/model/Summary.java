package com.mercia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Summary {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long summaryId;

	private Long totalRecords;
	private Long totalMatchedRecords;
	private Long totalMisMatchedRecords;
	private Long totalMissingRecords;
	private Long totalFailedRecords;
	
}
