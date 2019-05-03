package com.philosofy.nvn.philosofy.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "quotes")
public class Quote {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "quote")
    private String quote;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "type")
    private String type;

    public Quote(int id, String quote, String author, String category, Date date, String type) {
        this.id = id;
        this.quote = quote;
        this.author = author;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    @Ignore
    public Quote(String quote, String author, String category, Date date, String type) {
        this.quote = quote;
        this.author = author;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
