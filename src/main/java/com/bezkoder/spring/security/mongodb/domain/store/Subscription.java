package com.bezkoder.spring.security.mongodb.domain.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
@CompoundIndexes({
  @CompoundIndex(name = "user_active_idx", def = "{ 'userId': 1, 'isActive': 1 }"),
  @CompoundIndex(name = "user_type_idx", def = "{ 'userId': 1, 'subscriptionType': 1 }")
})
public class Subscription {
  @Id
  private String id;
  private String userId;
  private String username;
  private String subscriptionType; // BASIC, PREMIUM, ULTIMATE
  private BigDecimal monthlyPrice;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime nextBillingDate;
  private boolean isActive;
  private String status; // ACTIVE, CANCELLED, EXPIRED, SUSPENDED
  private String paymentMethod;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Subscription() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.isActive = true;
    this.status = "ACTIVE";
  }

  public Subscription(String userId, String username, String subscriptionType, BigDecimal monthlyPrice) {
    this();
    this.userId = userId;
    this.username = username;
    this.subscriptionType = subscriptionType;
    this.monthlyPrice = monthlyPrice;
    this.startDate = LocalDateTime.now();
    this.nextBillingDate = LocalDateTime.now().plusMonths(1);
    this.endDate = LocalDateTime.now().plusMonths(1);
  }

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public String getSubscriptionType() { return subscriptionType; }
  public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
  
  public BigDecimal getMonthlyPrice() { return monthlyPrice; }
  public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
  
  public LocalDateTime getStartDate() { return startDate; }
  public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
  
  public LocalDateTime getEndDate() { return endDate; }
  public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
  
  public LocalDateTime getNextBillingDate() { return nextBillingDate; }
  public void setNextBillingDate(LocalDateTime nextBillingDate) { this.nextBillingDate = nextBillingDate; }
  
  public boolean isActive() { return isActive; }
  public void setActive(boolean active) { isActive = active; }
  
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  
  public String getPaymentMethod() { return paymentMethod; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
