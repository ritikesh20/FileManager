package com.example.filemanager.trashbin;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecycleBinDao {

    @Insert
    void insert(RecycleBinItem item);

    @Query("SELECT * FROM recycle_bin ORDER BY deleted_at DESC")
    List<RecycleBinItem> getAll();

    @Query("DELETE FROM recycle_bin WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM recycle_bin")
    void deleteAll();

    @Update
    void update(RecycleBinItem item);

}
