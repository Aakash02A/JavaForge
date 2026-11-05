# 07 - Contact Book

A Java console application for adding, listing, searching, updating, and deleting contacts. `ContactService` keeps contact records in an in-memory `HashMap`, using a lower-case name as the lookup key.

## Features

- Add a contact with a name, phone number, and email.
- List all contacts.
- Search for a contact by name.
- Update a contact's name, phone number, or email.
- Delete a contact by name.
- Reject blank names and duplicate names (case-insensitive).

## Design and limitations

- `Contact` is the data model.
- `ContactService` contains the map-backed contact operations and duplicate checks.
- `Main` handles the console menu and user input.

Contacts are not saved to disk or a database, so they are cleared when the program exits. Names are the unique key; phone numbers and email addresses are not currently validated or required to be unique.

## Requirements and run

- JDK 21
- Maven

From this directory:

```bash
mvn clean package
java -cp target/classes com.javaforge.contact.Main
```

Run `com.javaforge.contact.Main` from an IDE if preferred. The menu accepts choices 1–6; choose 6 to exit.

## Tests

Run `mvn test` to compile the application and execute any configured tests. This project currently has no automated test classes; manually exercise each menu operation and duplicate-name handling.
