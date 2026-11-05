# 03 - Bank Account Simulator

## Project Idea

Build a console banking application that models different account products with an abstract `BankAccount` base class and specialized `SavingsAccount` and `CurrentAccount` implementations.

## Problem Statement

Different bank accounts follow different rules. A savings account must maintain a minimum balance and can earn interest, while a current account may use a configured overdraft. The simulator demonstrates how inheritance and polymorphism can express those rules without putting every condition in one large procedural method.

## Objectives

- Design an abstract account model with shared state and behavior.
- Use inheritance to implement savings and current account rules.
- Validate deposits, withdrawals, transfers, and account configuration.
- Record timestamped transaction history for each account.
- Provide a usable interactive console menu.
- Test business rules independently from console input.

## Features

- Create savings or current accounts.
- Deposit money and withdraw money.
- Transfer funds between two existing accounts.
- Enforce savings minimum balances.
- Enforce current-account overdraft limits.
- Apply interest to every savings account.
- View one account, all accounts, or transaction history.
- Start with demo accounts for immediate manual exploration.
- Reject malformed numeric input without terminating the application.

## Tech Stack

- Java 21
- Maven
- JUnit 5
- Object-oriented programming: abstraction, inheritance, polymorphism, and encapsulation
- `ArrayList` and unmodifiable collection views
- `LocalDateTime` and `DateTimeFormatter`
- `Scanner` for console input

## Architecture

```text
Main
  -> BankService
       -> BankAccount (abstract)
            -> SavingsAccount
            -> CurrentAccount
       -> Transaction
```

`BankAccount` owns common account data, deposits, and transaction storage. Each subclass overrides `withdraw` and supplies its account type. `BankService` coordinates account lookup, account creation, transfers, and the interactive workflows. `Main` is responsible only for displaying the menu and dispatching choices.

## Business Rules

### Savings accounts

- The initial deposit must be at least the configured minimum balance.
- A withdrawal succeeds only when the resulting balance remains at or above that minimum.
- Interest is calculated as `balance * rate / 100`.

### Current accounts

- A withdrawal may reduce the balance below zero.
- The balance cannot go below the negative overdraft limit.

### Transfers

- Source and destination accounts must both exist and be different.
- The amount must be positive.
- The source account's own withdrawal rules are applied.
- A successful transfer creates one withdrawal transaction on the source and one deposit transaction on the destination.

## Project Structure

```text
03-Bank-Account-Simulator/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/javaforge/bank/
    │   ├── Main.java
    │   ├── model/
    │   │   ├── BankAccount.java
    │   │   ├── CurrentAccount.java
    │   │   ├── SavingsAccount.java
    │   │   └── Transaction.java
    │   └── service/BankService.java
    └── test/java/com/javaforge/bank/
        ├── BankAccountTest.java
        └── BankServiceTest.java
```

## Setup and Run

From the repository root:

```bash
cd 03-Bank-Account-Simulator
mvn clean test
mvn package
java -cp target/classes com.javaforge.bank.Main
```

The project is intentionally in-memory. No database, credentials, or external service is required.

## Example Console Flow

1. Choose `6` to view the seeded demo accounts.
2. Choose `3`, enter `SA1001`, and try withdrawing more than the minimum-balance allowance.
3. Choose `4`, transfer `250` from `CA2001` to `CA2002`.
4. Choose `7` and inspect the source and destination histories.
5. Choose `8` to apply interest to all savings accounts.
6. Choose `9` to exit.

## Testing

The automated tests cover:

- Savings minimum-balance enforcement.
- Current-account overdraft behavior.
- Interest calculation and transaction recording.
- Rejection of negative account configuration values.
- Successful transfers and account balances.
- Rejection of unknown or same-account transfers.

## Learning Outcomes

After completing this project, a learner should be able to:

- Choose an abstract base class for shared domain behavior.
- Override methods when subclasses have different business rules.
- Keep mutable collections encapsulated.
- Separate console orchestration from domain/service logic.
- Write focused unit tests for state transitions.

## Limitations and Future Improvements

This is a learning simulator, not production banking software. It uses `double` values and in-memory storage, so it does not provide monetary precision, persistence, authentication, concurrency controls, or audit-grade durability. A production version should use `BigDecimal`, a database, authenticated users, and transactional persistence.
