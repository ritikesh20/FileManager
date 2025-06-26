package com.example.filemanager.favouritesection;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {FavouriteItem.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract FavouriteDao favouriteVideoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "favourite_Item_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // only for demo! Use async in production
                    .build();
        }
        return instance;
    }
}
