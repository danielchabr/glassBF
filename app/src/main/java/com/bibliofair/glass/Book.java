package com.bibliofair.glass;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by damell on 23/03/14.
 */
public class Book {
    private String author;
    private String title;
    private String isbn;

    public Book() {

    }

    public Book(String title, String author, String isbn) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public static Book parseBookFromJSON(String response) {
        Book book = new Book();
        Log.d("Book", response);
        try {
            JSONObject jObject = new JSONObject(response);
            Log.d("Book", jObject.toString());
            JSONArray arr = jObject.getJSONArray("Results");
            Log.d("Book", arr.toString());
            JSONObject oneBook = arr.getJSONObject(0);
            Log.d("Book", oneBook.toString());
            book.setAuthor(oneBook.getString("CREATOR"));
            book.setTitle(oneBook.getString("TITLE"));
        } catch (Exception e) {
            Log.d("Book", e.toString());
        }
        return book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
