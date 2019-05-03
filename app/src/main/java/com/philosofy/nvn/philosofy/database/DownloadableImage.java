package com.philosofy.nvn.philosofy.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloadable_images")
public class DownloadableImage {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "preview_image_url")
    private String previewImageUrl;

    @ColumnInfo(name = "large_image_url")
    private String largeImageUrl;

    @ColumnInfo(name = "views")
    private int views;

    @ColumnInfo(name = "page_no")
    private int pageNo;

    public DownloadableImage(int id, String previewImageUrl, String largeImageUrl, String category,
                             String color, int views, int pageNo) {
        this.id = id;
        this.previewImageUrl = previewImageUrl;
        this.largeImageUrl = largeImageUrl;
        this.category = category;
        this.color = color;
        this.views = views;
        this.pageNo = pageNo;
    }

    public int getId() {
        return id;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public String getColor() {
        return color;
    }

    public int getViews() {
        return views;
    }

    public String getCategory() {
        return category;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
