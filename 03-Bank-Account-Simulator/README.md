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
mvn clean test
mvn package
java -cp target/classes com.javaforge.bank.Main
```

The application starts with demo accounts so every menu operation can be tried immediately. It keeps data in memory and does not require an external database.

## 🧪 Testing

The JUnit suite verifies savings minimum-balance rules, current-account overdrafts, interest calculations, invalid configuration handling, and transfer behavior. Run it with:

```bash
mvn test
```

Transfers record one withdrawal on the source account and one deposit on the destination account, avoiding duplicate transaction entries.
