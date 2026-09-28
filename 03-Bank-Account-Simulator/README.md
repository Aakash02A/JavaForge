# 03 - Bank Account Simulator

A console-based banking simulator built with Core Java, demonstrating **Inheritance** through an abstract `BankAccount` base class with `SavingsAccount` and `CurrentAccount` subclasses.

## 🚀 Features

- **Create Account:** Open Savings or Current accounts with customizable parameters.
- **Deposit:** Add funds to any account.
- **Withdraw:** Withdraw with business rule validation (minimum balance for Savings, overdraft limit for Current).
- **Transfer:** Move funds between any two accounts.
- **Check Balance:** View detailed account information.
- **View All Accounts:** List all registered accounts.
- **Transaction History:** View timestamped log of all account activity.
- **Apply Interest:** Apply interest rate to all Savings accounts.

## 🛠️ Tech Stack & Concepts

- **Language:** Java 21
- **OOP Concepts:**
  - **Inheritance:** `SavingsAccount` and `CurrentAccount` extend abstract `BankAccount`.
  - **Abstraction:** Abstract `withdraw()` and `getAccountType()` methods.
  - **Polymorphism:** Overriding withdraw behavior per account type.
  - **Encapsulation:** Balance protection, transaction logging.
- **Collections:** `ArrayList` for accounts and transaction history.
- **Java Time API:** `LocalDateTime` for timestamped transactions.
- **Architecture:** Layered packaging (`model`, `service`, `Main`)

## 📂 Project Structure

```
03-Bank-Account-Simulator/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── javaforge/
                    └── bank/
                        ├── model/
                        │   ├── BankAccount.java
                        │   ├── SavingsAccount.java
                        │   ├── CurrentAccount.java
                        │   └── Transaction.java
                        ├── service/
                        │   └── BankService.java
                        └── Main.java
```

## ▶️ How to Run

```bash
cd 03-Bank-Account-Simulator
javac -d target/classes src/main/java/com/javaforge/bank/model/*.java src/main/java/com/javaforge/bank/service/*.java src/main/java/com/javaforge/bank/*.java
java -cp target/classes com.javaforge.bank.Main
```
