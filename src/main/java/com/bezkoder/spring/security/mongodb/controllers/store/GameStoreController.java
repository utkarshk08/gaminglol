package com.bezkoder.spring.security.mongodb.controllers.store;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.security.mongodb.domain.store.Game;
import com.bezkoder.spring.security.mongodb.domain.store.Purchase;
import com.bezkoder.spring.security.mongodb.domain.store.Subscription;
import com.bezkoder.spring.security.mongodb.domain.store.UserProfile;
import com.bezkoder.spring.security.mongodb.service.GameStoreService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/store")
public class GameStoreController {
  
  @Autowired
  private GameStoreService gameStoreService;

  // Game endpoints
  @GetMapping("/games")
  public ResponseEntity<List<Game>> getAllGames() {
    List<Game> games = gameStoreService.getAllGames();
    return ResponseEntity.ok(games);
  }

  @GetMapping("/games/paged")
  public ResponseEntity<Page<Game>> getGamesPaged(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<Game> games = gameStoreService.getGames(page, size);
    return ResponseEntity.ok(games);
  }

  @GetMapping("/games/{id}")
  public ResponseEntity<Game> getGameById(@PathVariable String id) {
    Optional<Game> game = gameStoreService.getGameById(id);
    return game.map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/games/category/{category}")
  public ResponseEntity<List<Game>> getGamesByCategory(@PathVariable String category) {
    List<Game> games = gameStoreService.getGamesByCategory(category);
    return ResponseEntity.ok(games);
  }

  @GetMapping("/games/search")
  public ResponseEntity<List<Game>> searchGames(@RequestParam String q) {
    List<Game> games = gameStoreService.searchGames(q);
    return ResponseEntity.ok(games);
  }

  @GetMapping("/games/price-range")
  public ResponseEntity<List<Game>> getGamesByPriceRange(
      @RequestParam BigDecimal minPrice,
      @RequestParam BigDecimal maxPrice) {
    List<Game> games = gameStoreService.getGamesByPriceRange(minPrice, maxPrice);
    return ResponseEntity.ok(games);
  }

  // User Profile endpoints
  @PostMapping("/users")
  public ResponseEntity<UserProfile> createUserProfile(@RequestBody CreateUserRequest request) {
    UserProfile profile = gameStoreService.createUserProfile(
        request.getUsername(), 
        request.getEmail(), 
        request.getFullName()
    );
    return ResponseEntity.ok(profile);
  }

  @GetMapping("/users/{username}")
  public ResponseEntity<UserProfile> getUserProfile(@PathVariable String username) {
    Optional<UserProfile> profile = gameStoreService.getUserProfile(username);
    return profile.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/users/{username}/wallet/add")
  public ResponseEntity<ApiResponse> addToWallet(
      @PathVariable String username,
      @RequestBody AddWalletRequest request) {
    boolean success = gameStoreService.addToWallet(username, request.getAmount());
    if (success) {
      return ResponseEntity.ok(new ApiResponse(true, "Wallet updated successfully"));
    } else {
      return ResponseEntity.badRequest().body(new ApiResponse(false, "User not found"));
    }
  }

  // Purchase endpoints
  @PostMapping("/purchases")
  public ResponseEntity<PurchaseResponse> purchaseGame(@RequestBody PurchaseRequest request) {
    GameStoreService.PurchaseResult result = gameStoreService.purchaseGame(
        request.getUsername(),
        request.getGameId(),
        request.getPaymentMethod()
    );
    
    if (result.isSuccess()) {
      return ResponseEntity.ok(new PurchaseResponse(true, result.getMessage(), result.getPurchase()));
    } else {
      return ResponseEntity.badRequest().body(new PurchaseResponse(false, result.getMessage(), null));
    }
  }

  @GetMapping("/users/{username}/purchases")
  public ResponseEntity<List<Purchase>> getUserPurchases(@PathVariable String username) {
    List<Purchase> purchases = gameStoreService.getUserPurchases(username);
    return ResponseEntity.ok(purchases);
  }

  // Subscription endpoints
  @PostMapping("/subscriptions")
  public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody CreateSubscriptionRequest request) {
    GameStoreService.SubscriptionResult result = gameStoreService.createSubscription(
        request.getUsername(),
        request.getSubscriptionType(),
        request.getPaymentMethod()
    );
    
    if (result.isSuccess()) {
      return ResponseEntity.ok(new SubscriptionResponse(true, result.getMessage(), result.getSubscription()));
    } else {
      return ResponseEntity.badRequest().body(new SubscriptionResponse(false, result.getMessage(), null));
    }
  }

  @GetMapping("/users/{username}/subscription")
  public ResponseEntity<Subscription> getUserSubscription(@PathVariable String username) {
    Optional<Subscription> subscription = gameStoreService.getUserSubscription(username);
    return subscription.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/users/{username}/subscription/cancel")
  public ResponseEntity<SubscriptionResponse> cancelSubscription(@PathVariable String username) {
    GameStoreService.SubscriptionResult result = gameStoreService.cancelSubscription(username);
    
    if (result.isSuccess()) {
      return ResponseEntity.ok(new SubscriptionResponse(true, result.getMessage(), result.getSubscription()));
    } else {
      return ResponseEntity.badRequest().body(new SubscriptionResponse(false, result.getMessage(), null));
    }
  }

  // Analytics endpoints
  @GetMapping("/analytics/top-spenders")
  public ResponseEntity<List<UserProfile>> getTopSpenders(@RequestParam(defaultValue = "10") int limit) {
    List<UserProfile> topSpenders = gameStoreService.getTopSpenders(limit);
    return ResponseEntity.ok(topSpenders);
  }

  @GetMapping("/analytics/top-buyers")
  public ResponseEntity<List<UserProfile>> getTopBuyers(@RequestParam(defaultValue = "10") int limit) {
    List<UserProfile> topBuyers = gameStoreService.getTopBuyers(limit);
    return ResponseEntity.ok(topBuyers);
  }

  @GetMapping("/analytics/stats")
  public ResponseEntity<StoreStats> getStoreStats() {
    long totalSubscriptions = gameStoreService.getTotalActiveSubscriptions();
    long totalPurchases = gameStoreService.getTotalCompletedPurchases();
    
    StoreStats stats = new StoreStats(totalSubscriptions, totalPurchases);
    return ResponseEntity.ok(stats);
  }

  // Request/Response DTOs
  public static class CreateUserRequest {
    private String username;
    private String email;
    private String fullName;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
  }

  public static class AddWalletRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
  }

  public static class PurchaseRequest {
    private String username;
    private String gameId;
    private String paymentMethod;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  }

  public static class CreateSubscriptionRequest {
    private String username;
    private String subscriptionType;
    private String paymentMethod;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  }

  public static class ApiResponse {
    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
  }

  public static class PurchaseResponse {
    private boolean success;
    private String message;
    private Purchase purchase;

    public PurchaseResponse(boolean success, String message, Purchase purchase) {
      this.success = success;
      this.message = message;
      this.purchase = purchase;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Purchase getPurchase() { return purchase; }
  }

  public static class SubscriptionResponse {
    private boolean success;
    private String message;
    private Subscription subscription;

    public SubscriptionResponse(boolean success, String message, Subscription subscription) {
      this.success = success;
      this.message = message;
      this.subscription = subscription;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Subscription getSubscription() { return subscription; }
  }

  public static class StoreStats {
    private long totalActiveSubscriptions;
    private long totalCompletedPurchases;

    public StoreStats(long totalActiveSubscriptions, long totalCompletedPurchases) {
      this.totalActiveSubscriptions = totalActiveSubscriptions;
      this.totalCompletedPurchases = totalCompletedPurchases;
    }

    public long getTotalActiveSubscriptions() { return totalActiveSubscriptions; }
    public long getTotalCompletedPurchases() { return totalCompletedPurchases; }
  }
}
