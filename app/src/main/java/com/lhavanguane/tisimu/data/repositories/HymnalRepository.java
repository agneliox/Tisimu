package com.lhavanguane.tisimu.data.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.lhavanguane.tisimu.data.database.TisimuDatabase;
import com.lhavanguane.tisimu.data.database.daos.HymnalDao;
import com.lhavanguane.tisimu.data.database.daos.UserHymnalSelectionDao;
import com.lhavanguane.tisimu.data.database.entities.Hymnal;
import com.lhavanguane.tisimu.data.database.entities.Section;
import com.lhavanguane.tisimu.data.database.entities.Song;
import com.lhavanguane.tisimu.data.database.entities.UserHymnalSelection;

import java.util.List;
public class HymnalRepository {
    private TisimuDatabase database;
    private FirebaseAuth mAuth;

    public HymnalRepository(Application application) {
        database = TisimuDatabase.getInstance(application);
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<List<Hymnal>> getAllHymnals() {
        return database.hymnalDao().getAllHymnals();
    }

    public LiveData<List<Hymnal>> getSelectedHymnals() {
        return database.hymnalDao().getSelectedHymnals();
    }

    public LiveData<Integer> getSelectedCount() {
        return database.hymnalDao().getSelectedCount();
    }

    public LiveData<List<Integer>> getSelectedHymnalIds() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "temp_user";
        return database.userHymnalSelectionDao().getSelectedHymnalIds(userId);
    }

    public LiveData<List<Section>> getSectionsForSelectedHymnals(List<Integer> hymnalIds) {
        if (hymnalIds == null || hymnalIds.isEmpty()) {
            return new MutableLiveData<>();
        }
        return database.sectionDao().getSectionsForSelectedHymnals(hymnalIds);
    }

    public LiveData<List<Song>> getSongsForSelectedHymnals(List<Integer> hymnalIds) {
        if (hymnalIds == null || hymnalIds.isEmpty()) {
            return new MutableLiveData<>();
        }
        return database.songDao().getSongsForSelectedHymnals(hymnalIds);
    }

    public void updateHymnalSelection(int hymnalId, boolean isSelected) {
        new UpdateSelectionTask(database).execute(hymnalId, isSelected);

        // Save to user-specific table for cloud sync
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            UserHymnalSelection selection = new UserHymnalSelection(userId, hymnalId, isSelected);
            new SaveUserSelectionTask(database).execute(selection);
        }
    }

    public void insertSampleHymnals() {
        new InsertSampleDataTask(database).execute();
    }

    // AsyncTasks for database operations
    private static class UpdateSelectionTask extends AsyncTask<Object, Void, Void> {
        private HymnalDao hymnalDao;

        UpdateSelectionTask(TisimuDatabase database) {
            hymnalDao = database.hymnalDao();
        }

        @Override
        protected Void doInBackground(Object... params) {
            int hymnalId = (int) params[0];
            boolean isSelected = (boolean) params[1];
            hymnalDao.updateSelection(hymnalId, isSelected);
            return null;
        }
    }

    private static class SaveUserSelectionTask extends AsyncTask<UserHymnalSelection, Void, Void> {
        private UserHymnalSelectionDao selectionDao;

        SaveUserSelectionTask(TisimuDatabase database) {
            selectionDao = database.userHymnalSelectionDao();
        }

        @Override
        protected Void doInBackground(UserHymnalSelection... selections) {
            selectionDao.insert(selections[0]);
            return null;
        }
    }

    private static class InsertSampleDataTask extends AsyncTask<Void, Void, Void> {
        private HymnalDao hymnalDao;

        InsertSampleDataTask(TisimuDatabase database) {
            hymnalDao = database.hymnalDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Check if data already exists
            // Insert sample hymnals
            Hymnal[] hymnals = {
                    new Hymnal("Harpa Cristã", "Traditional Brazilian gospel hymnal", "Portuguese", 640),
                    new Hymnal("Cantor Cristão", "Classic Portuguese hymns", "Portuguese", 520),
                    new Hymnal("Hinário Adventista", "Seventh-day Adventist hymnal", "Portuguese", 400),
                    new Hymnal("Hymns of Grace", "Modern worship hymns", "English", 350),
                    new Hymnal("Songs of Praise", "International worship collection", "Portuguese/English", 280)
            };

            for (Hymnal hymnal : hymnals) {
                hymnalDao.insert(hymnal);
            }
            return null;
        }
    }
}
