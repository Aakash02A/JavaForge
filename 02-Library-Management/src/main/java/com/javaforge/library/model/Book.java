package com.javaforge.library.model;

public class Book extends LibraryItem {
    private String genre;
    private String isbn;

    public Book(int id, String title, String author, String genre, String isbn) {
        super(id, title, author);
        this.genre = genre;
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String getItemType() {
        return "Book";
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println("Type        : " + getItemType());
        System.out.println("ID          : " + getId());
        System.out.println("Title       : " + getTitle());
        System.out.println("Author      : " + getAuthor());
        System.out.println("Genre       : " + genre);
        System.out.println("ISBN        : " + isbn);
        System.out.println("Status      : " + (isAvailable() ? "Available" : "Issued to " + getIssuedTo()));
        System.out.println("----------------------------------------");
    }

    @Override
    public String toString() {
        String status = isAvailable() ? "Available" : "Issued to: " + getIssuedTo();
        return "ID: " + getId() +
                ", Title: \"" + getTitle() + "\"" +
                ", Author: " + getAuthor() +
                ", Genre: " + genre +
                ", ISBN: " + isbn +
                ", Status: " + status;
    }
}
