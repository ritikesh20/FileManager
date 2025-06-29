package com.example.filemanager.safefolder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "safe_box")
public class SafeBoxFile {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String fileName;
    public String fileOriginalPath;
    public String fileSavePath;

    public SafeBoxFile() {
    }

    public SafeBoxFile(String fileName, String fileOriginalPath, String fileSavePath) {
        this.fileName = fileName;
        this.fileOriginalPath = fileOriginalPath;
        this.fileSavePath = fileSavePath;
    }
}
