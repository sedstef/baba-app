# Baba-App - Basic-Banking App
## Console-Based Banking Simulator

---

Baba-App is a **console-based secure banking application** written in **Java**.  
It allows users to register, authenticate, manage multiple bank accounts, and perform transactions such as deposits, 
withdrawals, and transfers.

The application was designed with a strong focus on **secure software development principles**, including:

- Encapsulation and abstraction  
- Input validation  
- Error handling  
- Logging  
- Cryptography for sensitive data  

No graphical user interface is available; all interactions are performed via the command line.

## Features

### User Authentication
- User registration and login using username and password
- Secure handling of credentials
- Authentication failures handled gracefully

### Account Management
- Create and manage multiple bank accounts per user
- Each account contains:
  - Account number
  - Balance (encapsulated)

### Transactions
- Deposit funds
- Withdraw funds (with overdraft protection)
- Transfer funds between accounts
- All transactions are validated before execution

### Security
- Sensitive data (accounts, transactions) is encrypted before storage
- Cryptographic logic is separated from business logic
- Passwords are never stored or logged in plaintext

### Logging
- Important events are logged:
  - Login attempts
  - Account creation
  - Transactions
- Sensitive information (passwords, balances, keys) is never logged

### Error Handling
- Invalid input is handled gracefully
- Clear but non-sensitive error messages
- No stack traces or internal details exposed to users

---

## 3. Application Architecture

The application follows a **layered and modular architecture**, separating concerns clearly between user interaction, 
business logic, security, and persistence.

```
+--------------------------------------------------+
|                    CLI Layer                     |
+--------------------------------------------------+
|                 Service Layer                 A  |
| +------------------------------------------+  u  |
| |                Domain Model              |  d  |
| +------------------------------------------+  i  |
| |              Business Logik              |  t  |
| +------------------------------------------+     |
+--------------------------------------------------+
|               Persistence Layer                  |
|--------------------------------------------------|
```

---

## 4. Design Principles Applied

### Encapsulation
- Sensitive fields (e.g., account balance) are private
- Access only via controlled methods

### Abstraction
- CLI is separated from business logic
- Security logic is isolated from services
- Clear responsibilities per class

### Input Validation
- All user input is validated before use
- Invalid commands or malformed data are rejected safely

### Error Handling
- Expected error cases (e.g., insufficient funds) are handled explicitly
- Application does not crash on invalid input
- Internal errors are not exposed to users

### Cryptography
- Sensitive data is encrypted when stored
- Cryptographic keys are managed separately
- No cryptographic logic leaks into unrelated parts of the system

---

## 5. How to Run the Application

### Requirements
- Java JDK 17 or newer
- IntelliJ IDEA (recommended) or any Java-compatible IDE

### Running in IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select **Open Project**
3. Choose the project folder
4. Let Gradle finish importing
5. Locate the main class
6. Click **Run**

No additional configuration is required.

---

## 6. Testing

- Unit tests are included using **JUnit**
- Tests cover:
  - Input parsing
  - Security components
  - Business logic
  - Error cases

Tests can be run directly from IntelliJ or via Gradle.

---

## 7. Notes

- This application is intended for **educational purposes**
- No real banking data should be used
- The focus is on **secure software design**, not real-world banking compliance
