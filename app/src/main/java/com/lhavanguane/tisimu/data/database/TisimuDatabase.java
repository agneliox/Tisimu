package com.lhavanguane.tisimu.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lhavanguane.tisimu.data.database.daos.HymnalDao;
import com.lhavanguane.tisimu.data.database.daos.SectionDao;
import com.lhavanguane.tisimu.data.database.daos.SongDao;
import com.lhavanguane.tisimu.data.database.daos.UserHymnalSelectionDao;
import com.lhavanguane.tisimu.data.database.entities.Hymnal;
import com.lhavanguane.tisimu.data.database.entities.Section;
import com.lhavanguane.tisimu.data.database.entities.Song;
import com.lhavanguane.tisimu.data.database.entities.UserHymnalSelection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Hymnal.class, UserHymnalSelection.class, Section.class, Song.class},
        version = 7, exportSchema = false)
public abstract class TisimuDatabase extends RoomDatabase {
    private static volatile TisimuDatabase instance;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract HymnalDao hymnalDao();
    public abstract UserHymnalSelectionDao userHymnalSelectionDao();
    public abstract SectionDao sectionDao();
    public abstract SongDao songDao();

    public static TisimuDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (TisimuDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TisimuDatabase.class,
                                    "tisimu_database"
                            ).fallbackToDestructiveMigration()
                            .addCallback(sampleDataCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    private static RoomDatabase.Callback sampleDataCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                TisimuDatabase database = instance;
                if (database != null) {
                    insertSampleData(database);
                }
            });
        }
    };

    private static void insertSampleData(TisimuDatabase database) {
        // Insert sample hymnals
        Hymnal hymnal1 = new Hymnal("Harpa Cristã", "Traditional Brazilian gospel hymnal", "Portuguese", 640);
        Hymnal hymnal2 = new Hymnal("Cantor Cristão", "Classic Portuguese hymns", "Portuguese", 520);
        Hymnal hymnal3 = new Hymnal("Hinário Adventista", "Seventh-day Adventist hymnal", "Portuguese", 400);

        database.hymnalDao().insert(hymnal1);
        database.hymnalDao().insert(hymnal2);
        database.hymnalDao().insert(hymnal3);

        // Insert sections for Harpa Cristã (hymnalId = 1)
        Section[] sections = {
                new Section(1, 1, "Louvor e Adoração", 1, 150),
                new Section(1, 2, "Natal", 151, 200),
                new Section(1, 3, "Páscoa", 201, 250),
                new Section(1, 4, "Fé e Esperança", 251, 350),
                new Section(1, 5, "Cristo é a Vida", 351, 500),
                new Section(1, 6, "Louvor Final", 501, 640)
        };

        for (Section section : sections) {
            database.sectionDao().insert(section);
        }

        // Insert sample songs
        Song[] songs = {
                new Song(1, 1, 1, "Grandioso És Tu", "Grandioso és Tu, Senhor da criação...\nOs céus proclamam Tua glória...", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 1, 2, "Santo, Santo, Santo", "Santo, Santo, Santo,\nSenhor Onipotente,\nCedo de manhã\nCantaremos Teu louvor.", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 1, 3, "Quão Grande És Tu", "Quão grande és Tu, quão grande és Tu,\nMeu coração Te exalta, meu Rei.", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 2, 151, "Noite Feliz", "Noite feliz, noite feliz,\nÓ Senhor, Deus de amor,\nPobrezinho nasceu em Belém.", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 2, 152, "Nasceu em Belém", "Nasceu em Belém, nasceu em Belém,\nO Salvador, Jesus nasceu em Belém.", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 3, 201, "Cristo Vive", "Cristo vive, aleluia!\nRessurgiu com poder.\nCristo vive, aleluia!\nPara sempre reinará.", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 4, 251, "Fiel Senhor", "Fiel Senhor, Teu amor é sem igual,\nTua graça me salvou.", "Santo", "Santo louvor.", true, null, null),
                new Song(1, 5, 351, "Maravilhoso É Jesus", "Maravilhoso é Jesus, meu Salvador,\nEle é a luz, o caminho e a verdade.", "Santo", "Santo louvor.", true, null, null)
        };

        for (Song song : songs) {
            database.songDao().insert(song);
        }

        // Pre-select the first hymnal for testing purposes
        database.userHymnalSelectionDao().insert(new UserHymnalSelection("temp_user", 1, true));
    }
}
