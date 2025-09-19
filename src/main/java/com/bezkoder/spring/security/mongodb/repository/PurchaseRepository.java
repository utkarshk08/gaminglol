package com.bezkoder.spring.security.mongodb.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bezkoder.spring.security.mongodb.domain.store.Purchase;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {
  
  List<Purchase> findByUserId(String userId);
  
  Page<Purchase> findByUserId(String userId, Pageable pageable);
  
  List<Purchase> findByUserIdOrderByPurchaseDateDesc(String userId);
  
  Optional<Purchase> findByUserIdAndGameId(String userId, String gameId);
  
  List<Purchase> findByStatus(String status);
  
  List<Purchase> findByPurchaseDateBetween(LocalDateTime startDate, LocalDateTime endDate);
  
  @Query("{ 'userId': ?0, 'status': 'COMPLETED' }")
  List<Purchase> findCompletedPurchasesByUserId(String userId);
  
  @Query("{ 'status': 'COMPLETED', 'purchaseDate': { $gte: ?0, $lte: ?1 } }")
  List<Purchase> findCompletedPurchasesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
  
  long countByUserIdAndStatus(String userId, String status);
  
  long countByGameIdAndStatus(String gameId, String status);
  
  long countByStatus(String status);
}
