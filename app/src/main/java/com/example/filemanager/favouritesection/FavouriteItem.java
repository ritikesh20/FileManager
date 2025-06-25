package com.example.filemanager.favouritesection;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_items")
public class FavouriteItem {

    @PrimaryKey(autoGenerate = true)
    int id;
    public String uri;
    public String name;
    public boolean isFolder;
    public String dateAdded;
    public String size;
    public int position;

    public FavouriteItem (){}

    public FavouriteItem(String uri, String name, boolean isFolder, String dateAdded,String size, int position) {
        this.uri = uri;
        this.name = name;
        this.isFolder = isFolder;
        this.dateAdded = dateAdded;
        this.size = size;
        this.position = position;

    }

    public int getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getSize() {
        return size;
    }

    public int getPosition() {
        return position;
    }
}
