package com.philosofy.nvn.philosofy.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorite_fonts")
public class FavoriteFont {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "font_name")
    private String fontName;

    @ColumnInfo(name = "lang")
    private String lang;

    @ColumnInfo(name = "added_at")
    private Date addedAt;

    @Ignore
    public FavoriteFont(String fontName, String lang, Date addedAt) {
        this.fontName = fontName;
        this.lang = lang;
        this.addedAt = addedAt;
    }

    public FavoriteFont(int id, String fontName, String lang, Date addedAt) {
        this.id = id;
        this.fontName = fontName;
        this.lang = lang;
        this.addedAt = addedAt;
    }

    public int getId() {
        return id;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public String getFontName() {
        return fontName;
    }

    public String getLang() {
        return lang;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }


}
