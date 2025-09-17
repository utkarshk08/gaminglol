package com.bezkoder.spring.security.mongodb.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.bezkoder.spring.security.mongodb.domain.wordhunt.WhScore;

public interface WhScoreRepository extends MongoRepository<WhScore, String> {
  Optional<WhScore> findByUserIdAndDate(String userId, LocalDate date);
  List<WhScore> findByDateOrderByScoreDesc(LocalDate date, Pageable pageable);
}


