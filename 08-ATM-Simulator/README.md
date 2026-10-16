# 08 - ATM Simulator

A Java console simulation of basic ATM operations for one in-memory account. It checks a PIN before allowing balance inquiries, deposits, and withdrawals.

## Features

- Authenticate using the demo PIN `1234`.
- Check the current balance.
- Deposit a positive amount.
- Withdraw a positive amount when funds are available.
- Handle invalid PINs, invalid menu input, and insufficient funds.

## Design and limitations

- `Account` stores an account number, PIN, and balance.
- `ATMService` implements authentication and balance operations.
- `InvalidPinException` and `InsufficientFundsException` represent expected failures.
- `Main` provides the interactive console menu.

The demo account is initialized in `Main` with account number `123456789` and balance `$1000.00`. The current menu does not provide a PIN-change operation. Account data is not persisted, the PIN is stored as plain text, and amounts use `double`; this project is not suitable for real financial use.

## Requirements and run

- JDK 21
- Maven

From this directory:

```bash
mvn clean package
java -cp target/classes com.javaforge.atm.Main
```

Enter `1234` when prompted. After authentication, choose 1 to check balance, 2 to withdraw, 3 to deposit, or 4 to exit.

## Tests

Run `mvn test` to compile the application and execute any configured tests. This project currently has no automated test classes; manually verify successful and rejected withdrawals and deposits.
