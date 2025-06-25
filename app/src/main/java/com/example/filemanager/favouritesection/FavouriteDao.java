package com.example.filemanager.favouritesection;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert
    void insert(FavouriteItem item);

    @Delete
    void delete(FavouriteItem item);

    @Update
    void update(FavouriteItem item);

    @Query("SELECT * FROM favourite_items")
    List<FavouriteItem> getAll();

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_items WHERE uri = :uri)")
    boolean isFavourite(String uri);

}
