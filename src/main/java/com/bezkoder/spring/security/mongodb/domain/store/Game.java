package com.bezkoder.spring.security.mongodb.domain.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "games")
public class Game {
  @Id
  private String id;
  
  @Indexed(unique = true)
  private String title;
  
  private String description;
  private String developer;
  private String publisher;
  private BigDecimal price;
  private String category;
  private List<String> tags;
  private String imageUrl;
  private double rating;
  private int reviewCount;
  private boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Game() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.isActive = true;
  }

  public Game(String title, String description, String developer, BigDecimal price, String category) {
    this();
    this.title = title;
    this.description = description;
    this.developer = developer;
    this.price = price;
    this.category = category;
  }

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public String getDeveloper() { return developer; }
  public void setDeveloper(String developer) { this.developer = developer; }
  
  public String getPublisher() { return publisher; }
  public void setPublisher(String publisher) { this.publisher = publisher; }
  
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  
  public List<String> getTags() { return tags; }
  public void setTags(List<String> tags) { this.tags = tags; }
  
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  
  public double getRating() { return rating; }
  public void setRating(double rating) { this.rating = rating; }
  
  public int getReviewCount() { return reviewCount; }
  public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
  
  public boolean isActive() { return isActive; }
  public void setActive(boolean active) { isActive = active; }
  
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
