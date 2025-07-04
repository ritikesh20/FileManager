package com.example.filemanager.trashbin;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recycle_bin")
public class RecycleBinItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "file_name")
    public String fileName;

    @ColumnInfo(name = "original_path")
    public String originalPath;

    @ColumnInfo(name = "deleted_path")
    public String deletedPath;

    @ColumnInfo(name = "deleted_at")
    public long deletedAt;

}
