package com.example.myapp;

public class DataClassBook {
    private String bookTitle;
    private String bookAuthor;
    private String bookCategory;
    private String bookDescription;
    private int bookQuantity;
    private String bookImage;
    private String key;

    public DataClassBook(String bookTitle, String bookAuthor, String bookCategory, String bookDescription, int bookQuantity, String bookImage) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookCategory = bookCategory;
        this.bookDescription = bookDescription;
        this.bookQuantity = bookQuantity;
        this.bookImage = bookImage;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public int getBookQuantity() {
        return bookQuantity;
    }

    public String getBookImage() {
        return bookImage;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClassBook() {

    }
}
