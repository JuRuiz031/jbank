# JBank

A Java CLI banking application that manages personal and business client accounts with various account types and financial operations.

## Features

- **Client Management**
  - Personal client account creation and management
  - Business client account creation and management
  - Client information validation and storage

- **Account Management**
  - Checking accounts
  - Savings accounts
  - Credit line accounts
  - Account balance tracking and transactions

- **Banking Operations**
  - Deposits and withdrawals
  - Account inquiries
  - Transaction history
  - Interest calculations for savings accounts (date and time not implemented)

## Technology Stack

- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** PostgreSQL, connected via JDBC
- **Testing:** JUnit 5
- **Logging:** SLF4J with Logback

## Project Structure

```
jbank/
├── src/
│   ├── main/
│   │   ├── java/com/jbank/
│   │   │   ├── controller/          # API controllers for clients and accounts
│   │   │   ├── model/               # Domain models and entities
│   │   │   ├── repository/          # Data access objects (DAOs)
│   │   │   ├── service/             # Business logic layer
│   │   │   ├── util/                # Utility classes
│   │   │   ├── validator/           # Input validation
│   │   │   └── App.java             # Main application entry point
│   │   └── resources/
│   │       ├── database.properties  # Database configuration
│   │       ├── schema.sql           # Database schema
│   │       └── logback.xml          # Logging configuration
│   └── test/
│       └── java/com/jbank/          # Unit tests
└── pom.xml                           # Maven configuration
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

## User Experience
Upon running the application, you'll be greeted with a main menu offering:

1. **Create New Customer** - Register a new personal or business client
2. **Handle Existing Customer** - Access and manage existing customer accounts
3. **Exit** - Quit the application

Follow the on-screen prompts to navigate through the banking operations.

## Project Dependencies

Key dependencies include:
- **PostgreSQL Driver** - Database connectivity
- **JUnit 5** - Unit testing framework
- **SLF4J & Logback** - Logging framework
## Author

JuRuiz031 (Juan Fernando Ruiz)
