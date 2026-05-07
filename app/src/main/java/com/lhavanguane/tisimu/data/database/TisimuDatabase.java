package com.lhavanguane.tisimu.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lhavanguane.tisimu.data.database.daos.HymnalDao;
import com.lhavanguane.tisimu.data.database.daos.UserHymnalSelectionDao;
import com.lhavanguane.tisimu.data.database.entities.Hymnal;
import com.lhavanguane.tisimu.data.database.entities.UserHymnalSelection;

@Database(entities = {Hymnal.class, UserHymnalSelection.class}, version = 1, exportSchema = false)
public abstract class TisimuDatabase extends RoomDatabase {
    public abstract HymnalDao hymnalDao();
    public abstract UserHymnalSelectionDao userHymnalSelectionDao();

    private static volatile TisimuDatabase instance;

    public static TisimuDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (TisimuDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TisimuDatabase.class,
                            "tisimu_database"
                    ).build();
                }
            }
        }
        return instance;
    }
}
