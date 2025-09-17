package com.bezkoder.spring.security.mongodb.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bezkoder.spring.security.mongodb.domain.wordhunt.WhDailySeed;

public interface WhDailySeedRepository extends MongoRepository<WhDailySeed, String> {
  Optional<WhDailySeed> findByDate(LocalDate date);
}


