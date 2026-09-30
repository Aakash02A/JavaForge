package com.javaforge.contact.service;

import com.javaforge.contact.model.Contact;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class ContactService {
    // HashMap to store contacts, using name as key
    private final Map<String, Contact> contacts;

    public ContactService() {
        this.contacts = new HashMap<>();
    }

    public void addContact(Contact contact) {
        if (contact == null || contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid contact data");
        }
        if (contacts.containsKey(contact.getName().toLowerCase())) {
            throw new IllegalArgumentException("Contact with this name already exists");
        }
        contacts.put(contact.getName().toLowerCase(), contact);
    }

    public void updateContact(String name, Contact updatedContact) {
        String key = name.toLowerCase();
        if (!contacts.containsKey(key)) {
            throw new IllegalArgumentException("Contact not found");
        }
        // Remove old if name changed
        if (!key.equals(updatedContact.getName().toLowerCase())) {
             if (contacts.containsKey(updatedContact.getName().toLowerCase())) {
                 throw new IllegalArgumentException("A contact with the new name already exists");
             }
             contacts.remove(key);
             contacts.put(updatedContact.getName().toLowerCase(), updatedContact);
        } else {
             contacts.put(key, updatedContact);
        }
    }

    public void deleteContact(String name) {
        if (contacts.remove(name.toLowerCase()) == null) {
            throw new IllegalArgumentException("Contact not found");
        }
    }

    public Contact getContact(String name) {
        return contacts.get(name.toLowerCase());
    }

    public Collection<Contact> getAllContacts() {
        return contacts.values();
    }
}
