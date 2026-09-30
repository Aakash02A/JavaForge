package com.javaforge.contact;

import com.javaforge.contact.model.Contact;
import com.javaforge.contact.service.ContactService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ContactService contactService = new ContactService();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Contact Book!");

        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Contact");
            System.out.println("2. View All Contacts");
            System.out.println("3. Search Contact");
            System.out.println("4. Update Contact");
            System.out.println("5. Delete Contact");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter phone number: ");
                    String phone = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    try {
                        contactService.addContact(new Contact(name, phone, email));
                        System.out.println("Contact added successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.println("\nAll Contacts:");
                    if (contactService.getAllContacts().isEmpty()) {
                        System.out.println("No contacts found.");
                    } else {
                        for (Contact c : contactService.getAllContacts()) {
                            System.out.println(c);
                        }
                    }
                    break;
                case 3:
                    System.out.print("Enter name to search: ");
                    String searchName = scanner.nextLine();
                    Contact found = contactService.getContact(searchName);
                    if (found != null) {
                        System.out.println("Contact found: " + found);
                    } else {
                        System.out.println("Contact not found.");
                    }
                    break;
                case 4:
                    System.out.print("Enter name to update: ");
                    String updateName = scanner.nextLine();
                    Contact existing = contactService.getContact(updateName);
                    if (existing != null) {
                        System.out.print("Enter new name (leave blank to keep current: " + existing.getName() + "): ");
                        String newName = scanner.nextLine();
                        if (newName.trim().isEmpty()) newName = existing.getName();

                        System.out.print("Enter new phone (leave blank to keep current: " + existing.getPhoneNumber() + "): ");
                        String newPhone = scanner.nextLine();
                        if (newPhone.trim().isEmpty()) newPhone = existing.getPhoneNumber();

                        System.out.print("Enter new email (leave blank to keep current: " + existing.getEmail() + "): ");
                        String newEmail = scanner.nextLine();
                        if (newEmail.trim().isEmpty()) newEmail = existing.getEmail();

                        try {
                            contactService.updateContact(updateName, new Contact(newName, newPhone, newEmail));
                            System.out.println("Contact updated successfully.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Contact not found.");
                    }
                    break;
                case 5:
                    System.out.print("Enter name to delete: ");
                    String deleteName = scanner.nextLine();
                    try {
                        contactService.deleteContact(deleteName);
                        System.out.println("Contact deleted successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting Contact Book...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
