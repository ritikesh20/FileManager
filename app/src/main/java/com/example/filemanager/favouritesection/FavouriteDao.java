package com.example.filemanager.favouritesection;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert
    void insert(FavouriteItem favouriteItem);

    @Delete
    void delete(FavouriteItem favouriteItem);

    @Query("SELECT * FROM favorite_box ORDER BY addedTime DESC")
    List<FavouriteItem> getAllFavorites();


}
