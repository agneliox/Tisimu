package com.lhavanguane.tisimu.data.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.lhavanguane.tisimu.data.database.TisimuDatabase;
import com.lhavanguane.tisimu.data.database.daos.SectionDao;
import com.lhavanguane.tisimu.data.database.daos.SongDao;
import com.lhavanguane.tisimu.data.database.entities.Section;
import com.lhavanguane.tisimu.data.database.entities.Song;

public class SongRepository {
    private TisimuDatabase database;

    public SongRepository(Application application) {
        database = TisimuDatabase.getInstance(application);
    }

    public LiveData<Song> getSongById(int songId) {
        return database.songDao().getSongById(songId);
    }

    public void insertSampleData() {
        new InsertSampleSongsTask(database).execute();
    }

    private static class InsertSampleSongsTask extends AsyncTask<Void, Void, Void> {
        private SectionDao sectionDao;
        private SongDao songDao;

        InsertSampleSongsTask(TisimuDatabase database) {
            sectionDao = database.sectionDao();
            songDao = database.songDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check if data exists
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
                sectionDao.insert(section);
            }

            // Insert sample songs
            Song[] songs = {
                    new Song(1, 1, 1, "Grandioso És Tu", "Grandioso és Tu, Senhor da criação...\nOs céus proclamam Tua glória...\n\nEstrelas e terra, montes e mar,\nTudo proclama o Teu grande amor.", "", "", true, null, ""),
                    new Song(1, 1, 2, "Santo, Santo, Santo", "Santo, Santo, Santo,\nSenhor Onipotente,\nCedo de manhã\nCantaremos Teu louvor.", "", "", true, null, ""),
                    new Song(1, 1, 3, "Quão Grande És Tu", "Quão grande és Tu, quão grande és Tu,\nMeu coração Te exalta, meu Rei.\nQuão grande és Tu, quão grande és Tu,\nPra sempre Te adorarei.", "", "", true, null, ""),
                    new Song(1, 2, 151, "Noite Feliz", "Noite feliz, noite feliz,\nÓ Senhor, Deus de amor,\nPobrezinho nasceu em Belém,\nEis na manjedoura o nosso bem.", "", "", true, null, ""),
                    new Song(1, 2, 152, "Nasceu em Belém", "Nasceu em Belém, nasceu em Belém,\nO Salvador, Jesus nasceu em Belém.\nNasceu em Belém, nasceu em Belém,\nCantai, cantai louvores ao Rei dos reis!", "", "", true, null, ""),
                    new Song(1, 3, 201, "Cristo Vive", "Cristo vive, aleluia!\nRessurgiu com poder.\nCristo vive, aleluia!\nPara sempre reinará.", "", "", true, null, ""),
                    new Song(1, 4, 251, "Fiel Senhor", "Fiel Senhor, Teu amor é sem igual,\nTua graça me salvou,\nPra sempre Te louvarei,\nMeu amado Redentor.", "", "", true, null, ""),
                    new Song(1, 5, 351, "Maravilhoso É Jesus", "Maravilhoso é Jesus, meu Salvador,\nEle é a luz, o caminho e a verdade.\nTodo poder Ele tem, venceu a morte,\nMeu Redentor vive para sempre, aleluia!", "", "", true, null, ""),
                    new Song(1, 6, 501, "Louvor Final", "Louvor final, louvor final,\nPra sempre Te adorarei", "", "", true, null, "")
            };

            for (Song song : songs) {
                songDao.insert(song);
            }

            return null;
        }
    }
}
