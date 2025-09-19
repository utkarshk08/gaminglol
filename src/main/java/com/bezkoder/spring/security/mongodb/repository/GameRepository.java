package com.bezkoder.spring.security.mongodb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bezkoder.spring.security.mongodb.domain.store.Game;

public interface GameRepository extends MongoRepository<Game, String> {
  
  List<Game> findByIsActiveTrue();
  
  Page<Game> findByIsActiveTrue(Pageable pageable);
  
  List<Game> findByCategoryAndIsActiveTrue(String category);
  
  List<Game> findByDeveloperAndIsActiveTrue(String developer);
  
  @Query("{ 'title': { $regex: ?0, $options: 'i' }, 'isActive': true }")
  List<Game> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
  
  @Query("{ 'tags': { $in: ?0 }, 'isActive': true }")
  List<Game> findByTagsInAndIsActiveTrue(List<String> tags);
  
  List<Game> findByPriceBetweenAndIsActiveTrue(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
  
  List<Game> findByRatingGreaterThanEqualAndIsActiveTrue(double minRating);
  
  Optional<Game> findByTitleAndIsActiveTrue(String title);
}
