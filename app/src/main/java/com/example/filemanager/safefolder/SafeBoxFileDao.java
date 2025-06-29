package com.example.filemanager.safefolder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SafeBoxFileDao {

    @Insert
    void insert(SafeBoxFile safeBoxFile);

    @Delete
    void delete(SafeBoxFile safeBoxFile);


    @Query("SELECT * FROM safe_box")
    List<SafeBoxFile> getAllSafeBoxFiles();

    

}
