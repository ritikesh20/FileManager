package com.example.filemanager.favouritesection;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favorite_box")

public class FavouriteItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String filePath;

    private String fileName;

    private long addedTime;


    public FavouriteItem(String filePath, String fileName, long addedTime) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.addedTime = addedTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public long getAddedTime() {
        return addedTime;
    }
}
