package com.bezkoder.spring.security.mongodb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bezkoder.spring.security.mongodb.domain.store.UserProfile;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
  
  Optional<UserProfile> findByUsername(String username);
  
  Optional<UserProfile> findByEmail(String email);
  
  List<UserProfile> findByIsPremiumMemberTrue();
  
  @Query("{ 'totalSpent': { $gte: ?0 } }")
  List<UserProfile> findByTotalSpentGreaterThanEqual(java.math.BigDecimal amount);
  
  @Query("{ 'totalGamesPurchased': { $gte: ?0 } }")
  List<UserProfile> findByTotalGamesPurchasedGreaterThanEqual(int count);
  
  @Query("{ 'favoriteCategories': { $in: ?0 } }")
  List<UserProfile> findByFavoriteCategoriesIn(List<String> categories);
  
  @Query(value = "{}", sort = "{ 'totalSpent': -1 }")
  List<UserProfile> findTopSpenders(Pageable pageable);
  
  @Query(value = "{}", sort = "{ 'totalGamesPurchased': -1 }")
  List<UserProfile> findTopBuyers(Pageable pageable);
}
