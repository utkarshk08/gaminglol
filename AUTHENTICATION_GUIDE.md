# GameStore Authentication Guide

## Overview

The GameStore application now features a complete authentication system with separate login and signup pages, replacing the previous inline profile creation system.

## New Authentication Flow

### 1. Home Page (`/home.html`)
- **Purpose**: Landing page for unauthenticated users
- **Features**: 
  - Welcome message and app description
  - Sign In and Create Account buttons
  - Feature showcase
- **Behavior**: Automatically redirects authenticated users to the main app

### 2. Sign Up Page (`/signup.html`)
- **Purpose**: User registration with account creation
- **Features**:
  - Username, email, full name, and password fields
  - Password strength validation with visual indicators
  - Real-time form validation
  - Creates both authentication account and user profile
- **Validation**:
  - Username: 3-20 characters
  - Email: Valid email format
  - Password: Minimum 6 characters with uppercase, lowercase, and number
  - Password confirmation matching

### 3. Login Page (`/login.html`)
- **Purpose**: User authentication
- **Features**:
  - Username and password fields
  - JWT-based authentication with cookies
  - Error handling and success messages
  - Automatic redirect to main app on success

### 4. Main Application (`/`)
- **Purpose**: Authenticated user dashboard
- **Features**:
  - Full game store functionality
  - User profile management
  - Wallet and subscription management
  - Logout functionality
- **Protection**: Requires authentication, redirects to home page if not logged in

## Technical Implementation

### Backend Changes
- **AuthController**: Added `/api/auth/signout` endpoint for logout
- **JWT Authentication**: Uses secure HTTP-only cookies
- **User Management**: Integrates with existing user profile system

### Frontend Changes
- **Authentication Check**: Validates JWT tokens stored in localStorage
- **Route Protection**: Main app requires authentication
- **User State Management**: Stores user info in localStorage
- **Automatic Redirects**: Seamless navigation between authenticated and unauthenticated states

## User Experience Flow

1. **New User**:
   - Visits `/home.html` → Clicks "Create Account" → Fills signup form → Redirected to login
   - Logs in → Redirected to main app with full functionality

2. **Returning User**:
   - Visits any page → Automatically redirected to main app if authenticated
   - If not authenticated → Redirected to home page

3. **Logout**:
   - Click logout button → Clears session → Redirected to home page

## Security Features

- **JWT Tokens**: Secure authentication with HTTP-only cookies
- **Password Validation**: Strong password requirements
- **Session Management**: Proper logout with token cleanup
- **Route Protection**: Unauthorized access prevention
- **Input Validation**: Client and server-side validation

## API Endpoints

- `POST /api/auth/signup` - User registration
- `POST /api/auth/signin` - User login
- `POST /api/auth/signout` - User logout
- `POST /api/store/users` - Create user profile (after signup)

## Files Modified/Created

### New Files
- `src/main/resources/static/home.html` - Landing page
- `src/main/resources/static/login.html` - Login page
- `src/main/resources/static/signup.html` - Registration page
- `AUTHENTICATION_GUIDE.md` - This documentation

### Modified Files
- `src/main/resources/static/index.html` - Updated header with logout
- `src/main/resources/static/app.js` - Added authentication logic
- `src/main/resources/static/styles.css` - Added auth button styles
- `src/main/java/.../AuthController.java` - Added logout endpoint

## Testing the Authentication Flow

1. **Start the application**: `./mvnw spring-boot:run`
2. **Visit**: `http://localhost:8080`
3. **Test flow**:
   - Should redirect to `/home.html`
   - Click "Create Account" → Fill form → Submit
   - Should redirect to login page
   - Login with created credentials
   - Should redirect to main app with full functionality
   - Test logout → Should return to home page

## Benefits of New System

- **Better UX**: Clear separation of authentication and app functionality
- **Security**: Proper JWT-based authentication
- **Scalability**: Easy to extend with additional auth features
- **Maintainability**: Clean separation of concerns
- **Professional**: Standard authentication flow users expect
