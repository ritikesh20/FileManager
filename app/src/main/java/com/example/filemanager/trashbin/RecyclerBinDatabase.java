package com.example.filemanager.trashbin;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RecycleBinItem.class}, version = 2)
public abstract class RecyclerBinDatabase extends RoomDatabase {

    private static volatile RecyclerBinDatabase INSTANCE;

    public abstract RecycleBinDao recycleBinDao();

    public static synchronized RecyclerBinDatabase getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RecyclerBinDatabase.class,
                    "trash_bin"
            ).fallbackToDestructiveMigration().build();
        }

        return INSTANCE;

    }

}
