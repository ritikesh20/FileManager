package com.example.filemanager.favouritesection;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavouriteItem.class}, version = 1, exportSchema = false)
public abstract class FavouriteDatabase extends RoomDatabase {

    private static FavouriteDatabase instance;

    public abstract FavouriteDao favouriteDao();

    public static synchronized FavouriteDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    FavouriteDatabase.class,
                    "Favorite_Dojo"
            ).fallbackToDestructiveMigration().build();
        }


        return instance;
    }

}
