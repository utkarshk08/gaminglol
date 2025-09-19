package com.bezkoder.spring.security.mongodb.domain.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_profiles")
public class UserProfile {
  @Id
  private String id;
  
  @Indexed(unique = true)
  private String username;
  
  private String email;
  private String fullName;
  private BigDecimal walletBalance;
  private int totalGamesPurchased;
  private BigDecimal totalSpent;
  private String preferredPaymentMethod;
  private List<String> favoriteCategories;
  private LocalDateTime memberSince;
  private LocalDateTime lastLogin;
  private boolean isPremiumMember;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UserProfile() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.walletBalance = BigDecimal.ZERO;
    this.totalGamesPurchased = 0;
    this.totalSpent = BigDecimal.ZERO;
    this.isPremiumMember = false;
  }

  public UserProfile(String username, String email, String fullName) {
    this();
    this.username = username;
    this.email = email;
    this.fullName = fullName;
    this.memberSince = LocalDateTime.now();
    this.lastLogin = LocalDateTime.now();
  }

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  
  public BigDecimal getWalletBalance() { return walletBalance; }
  public void setWalletBalance(BigDecimal walletBalance) { this.walletBalance = walletBalance; }
  
  public int getTotalGamesPurchased() { return totalGamesPurchased; }
  public void setTotalGamesPurchased(int totalGamesPurchased) { this.totalGamesPurchased = totalGamesPurchased; }
  
  public BigDecimal getTotalSpent() { return totalSpent; }
  public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
  
  public String getPreferredPaymentMethod() { return preferredPaymentMethod; }
  public void setPreferredPaymentMethod(String preferredPaymentMethod) { this.preferredPaymentMethod = preferredPaymentMethod; }
  
  public List<String> getFavoriteCategories() { return favoriteCategories; }
  public void setFavoriteCategories(List<String> favoriteCategories) { this.favoriteCategories = favoriteCategories; }
  
  public LocalDateTime getMemberSince() { return memberSince; }
  public void setMemberSince(LocalDateTime memberSince) { this.memberSince = memberSince; }
  
  public LocalDateTime getLastLogin() { return lastLogin; }
  public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
  
  public boolean isPremiumMember() { return isPremiumMember; }
  public void setPremiumMember(boolean premiumMember) { isPremiumMember = premiumMember; }
  
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
