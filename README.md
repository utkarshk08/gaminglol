# GameStore - Digital Gaming Hub

A modern game store application built with Spring Boot and MongoDB Atlas, demonstrating e-commerce functionality with game purchases, subscription management, and user analytics.

## 🎮 Features

- **Game Catalog**: Browse and search through a curated collection of games
- **User Profiles**: Create profiles with wallet management and purchase history
- **Game Purchases**: Buy games with wallet-based payments
- **Subscription Plans**: Subscribe to Basic, Premium, or Ultimate plans
- **Analytics Dashboard**: View top spenders, buyers, and store statistics
- **Modern UI**: Responsive design with smooth animations and notifications

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.1.0, Spring Data MongoDB
- **Database**: MongoDB Atlas (Cloud)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Security**: Spring Security (configured for public access)
- **Build Tool**: Maven

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MongoDB Atlas account

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd game-store
   ```

2. **Configure MongoDB Atlas**
   - Create a MongoDB Atlas cluster
   - Get your connection string
   - Update `src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/gamestore
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - Open http://localhost:8080 in your browser
   - Create a user profile to start using the store

## 📚 API Documentation

### Games

#### Get All Games
```http
GET /api/store/games
```

#### Get Games (Paginated)
```http
GET /api/store/games/paged?page=0&size=10
```

#### Get Game by ID
```http
GET /api/store/games/{id}
```

#### Search Games
```http
GET /api/store/games/search?q=cyberpunk
```

#### Get Games by Category
```http
GET /api/store/games/category/RPG
```

### User Management

#### Create User Profile
```http
POST /api/store/users
Content-Type: application/json

{
  "username": "gamer123",
  "email": "gamer@example.com",
  "fullName": "John Gamer"
}
```

#### Get User Profile
```http
GET /api/store/users/{username}
```

#### Add to Wallet
```http
POST /api/store/users/{username}/wallet/add
Content-Type: application/json

{
  "amount": 50.00
}
```

### Purchases

#### Purchase Game
```http
POST /api/store/purchases
Content-Type: application/json

{
  "username": "gamer123",
  "gameId": "game-id-here",
  "paymentMethod": "WALLET"
}
```

#### Get User Purchases
```http
GET /api/store/users/{username}/purchases
```

### Subscriptions

#### Create Subscription
```http
POST /api/store/subscriptions
Content-Type: application/json

{
  "username": "gamer123",
  "subscriptionType": "PREMIUM",
  "paymentMethod": "WALLET"
}
```

#### Get User Subscription
```http
GET /api/store/users/{username}/subscription
```

#### Cancel Subscription
```http
POST /api/store/users/{username}/subscription/cancel
```

### Analytics

#### Get Top Spenders
```http
GET /api/store/analytics/top-spenders?limit=10
```

#### Get Top Buyers
```http
GET /api/store/analytics/top-buyers?limit=10
```

#### Get Store Statistics
```http
GET /api/store/analytics/stats
```

## 🎯 Game Store Features

### Subscription Plans

| Plan | Price | Features |
|------|-------|----------|
| **Basic** | $9.99/month | Access to free games, Basic support |
| **Premium** | $19.99/month | All Basic features, Premium games library, Priority support |
| **Ultimate** | $29.99/month | All Premium features, Early access to new games, Exclusive content |

### Sample Games

The application comes pre-loaded with 20 popular games across various categories:
- **RPG**: Cyberpunk 2077, The Witcher 3, Baldur's Gate 3, Elden Ring
- **Action**: Grand Theft Auto V, Assassin's Creed Mirage, Spider-Man 2
- **FPS**: Valorant, Counter-Strike 2, Call of Duty: Modern Warfare III
- **Sports**: FIFA 24, NBA 2K24
- **Free-to-Play**: Valorant, League of Legends, Counter-Strike 2

## 🏗️ Architecture

### Domain Models

- **Game**: Game catalog with metadata, pricing, and ratings
- **UserProfile**: User information, wallet balance, purchase history
- **Purchase**: Transaction records for game purchases
- **Subscription**: Subscription management and billing

### Key Components

- **GameStoreService**: Business logic for purchases, subscriptions, and analytics
- **GameStoreController**: REST API endpoints
- **Repositories**: MongoDB data access with custom queries
- **DataInitializer**: Populates the store with sample games

## 🎨 Frontend Features

- **Responsive Design**: Works on desktop, tablet, and mobile
- **Interactive Game Cards**: Hover effects and smooth animations
- **Real-time Notifications**: Success/error messages with auto-dismiss
- **Wallet Management**: Add funds and track balance
- **Purchase History**: View all purchased games
- **Subscription Management**: Subscribe/cancel plans
- **Analytics Dashboard**: Live store statistics

## 🔧 Configuration

### Application Properties
```properties
# MongoDB Atlas Connection
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/gamestore

# Server Configuration
server.port=8080

# Security (Public Access)
spring.security.user.name=admin
spring.security.user.password=admin
```

### MongoDB Collections
- `games`: Game catalog
- `user_profiles`: User information and statistics
- `purchases`: Purchase transactions
- `subscriptions`: Subscription records

## 🚀 Deployment

### Local Development
```bash
./mvnw spring-boot:run
```

### Production Build
```bash
./mvnw clean package
java -jar target/spring-boot-mongodb-login-0.0.1-SNAPSHOT.jar
```

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/spring-boot-mongodb-login-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📊 Database Schema

### Game Document
```json
{
  "_id": "ObjectId",
  "title": "Cyberpunk 2077",
  "description": "An open-world, action-adventure story",
  "developer": "CD Projekt RED",
  "publisher": "CD Projekt RED",
  "price": 59.99,
  "category": "RPG",
  "tags": ["rpg", "popular", "trending"],
  "imageUrl": "https://...",
  "rating": 4.2,
  "reviewCount": 1500,
  "isActive": true,
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

### UserProfile Document
```json
{
  "_id": "ObjectId",
  "username": "gamer123",
  "email": "gamer@example.com",
  "fullName": "John Gamer",
  "walletBalance": 25.50,
  "totalGamesPurchased": 3,
  "totalSpent": 89.97,
  "preferredPaymentMethod": "WALLET",
  "favoriteCategories": ["RPG", "Action"],
  "memberSince": "2025-01-01T00:00:00",
  "lastLogin": "2025-01-15T10:30:00",
  "isPremiumMember": true
}
```

## 🎯 Use Cases Demonstrated

1. **E-commerce**: Game purchasing with wallet management
2. **Subscription Services**: Recurring billing and plan management
3. **User Analytics**: Tracking spending patterns and user behavior
4. **MongoDB Atlas**: Cloud database integration with Spring Boot
5. **RESTful APIs**: Clean API design with proper HTTP methods
6. **Modern Frontend**: Responsive UI with JavaScript interactions

## 🔍 Monitoring & Analytics

The application includes built-in analytics for:
- Top spending users
- Most active buyers
- Store-wide statistics
- Subscription metrics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions or issues:
1. Check the API documentation above
2. Review the application logs
3. Ensure MongoDB Atlas connection is working
4. Verify all required fields are provided in API requests

---

**Happy Gaming! 🎮**