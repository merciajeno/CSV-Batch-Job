package com.mercia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mercia.model.Summary;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long>{

}
