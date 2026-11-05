package com.javaforge.library.model;

public abstract class LibraryItem {
    private int id;
    private String title;
    private String author;
    private boolean isAvailable;
    private String issuedTo;

    public LibraryItem(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.issuedTo = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
    }

    public boolean issueItem(String borrowerName) {
        if (!isAvailable) {
            return false;
        }
        this.isAvailable = false;
        this.issuedTo = borrowerName;
        return true;
    }

    public boolean returnItem() {
        if (isAvailable) {
            return false;
        }
        this.isAvailable = true;
        this.issuedTo = "";
        return true;
    }

    public abstract String getItemType();

    public abstract void displayDetails();

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Issued to " + issuedTo;
        return "[" + getItemType() + "] ID: " + id +
                ", Title: \"" + title + "\"" +
                ", Author: " + author +
                ", Status: " + status;
    }
}
