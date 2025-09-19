package com.bezkoder.spring.security.mongodb.domain.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "purchases")
@CompoundIndexes({
  @CompoundIndex(name = "user_game_idx", def = "{ 'userId': 1, 'gameId': 1 }", unique = true),
  @CompoundIndex(name = "user_date_idx", def = "{ 'userId': 1, 'purchaseDate': -1 }")
})
public class Purchase {
  @Id
  private String id;
  private String userId;
  private String username;
  private String gameId;
  private String gameTitle;
  private BigDecimal price;
  private String paymentMethod;
  private String status; // PENDING, COMPLETED, FAILED, REFUNDED
  private LocalDateTime purchaseDate;
  private LocalDateTime createdAt;

  public Purchase() {
    this.createdAt = LocalDateTime.now();
    this.purchaseDate = LocalDateTime.now();
    this.status = "PENDING";
  }

  public Purchase(String userId, String username, String gameId, String gameTitle, BigDecimal price) {
    this();
    this.userId = userId;
    this.username = username;
    this.gameId = gameId;
    this.gameTitle = gameTitle;
    this.price = price;
  }

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public String getGameId() { return gameId; }
  public void setGameId(String gameId) { this.gameId = gameId; }
  
  public String getGameTitle() { return gameTitle; }
  public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
  
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  
  public String getPaymentMethod() { return paymentMethod; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  
  public LocalDateTime getPurchaseDate() { return purchaseDate; }
  public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
  
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
