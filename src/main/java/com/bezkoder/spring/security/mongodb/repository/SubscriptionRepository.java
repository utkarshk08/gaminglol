package com.bezkoder.spring.security.mongodb.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bezkoder.spring.security.mongodb.domain.store.Subscription;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
  
  List<Subscription> findByUserId(String userId);
  
  Optional<Subscription> findByUserIdAndIsActiveTrue(String userId);
  
  List<Subscription> findByIsActiveTrue();
  
  List<Subscription> findByStatus(String status);
  
  List<Subscription> findBySubscriptionType(String subscriptionType);
  
  @Query("{ 'userId': ?0, 'isActive': true, 'endDate': { $gte: ?1 } }")
  Optional<Subscription> findActiveSubscriptionByUserIdAndDate(String userId, LocalDateTime date);
  
  @Query("{ 'nextBillingDate': { $lte: ?0 }, 'isActive': true }")
  List<Subscription> findSubscriptionsDueForBilling(LocalDateTime date);
  
  @Query("{ 'endDate': { $lte: ?0 }, 'isActive': true }")
  List<Subscription> findExpiredActiveSubscriptions(LocalDateTime date);
  
  long countByUserIdAndIsActiveTrue(String userId);
  
  long countBySubscriptionTypeAndIsActiveTrue(String subscriptionType);
  
  long countByIsActiveTrue();
}
