package com.lhavanguane.tisimu.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lhavanguane.tisimu.data.database.entities.Hymnal;
import com.lhavanguane.tisimu.data.repositories.HymnalRepository;

import java.util.List;

public class HymnalViewModel extends AndroidViewModel {
    private HymnalRepository repository;
    private LiveData<List<Hymnal>> allHymnals;
    private LiveData<List<Hymnal>> selectedHymnals;
    private LiveData<Integer> selectedCount;
    public HymnalViewModel(@NonNull Application application) {
        super(application);
        repository = new HymnalRepository(application);
        allHymnals = repository.getAllHymnals();
        selectedHymnals = repository.getSelectedHymnals();
        selectedCount = repository.getSelectedCount();

        // Insert sample data (only once)
        repository.insertSampleHymnals();
    }
    public LiveData<List<Hymnal>> getAllHymnals() {
        return allHymnals;
    }
    public LiveData<List<Hymnal>> getSelectedHymnals() {
        return selectedHymnals;
    }
    public LiveData<Integer> getSelectedCount() {
        return selectedCount;
    }
    public void updateHymnalSelection(int hymnalId, boolean isSelected) {
        repository.updateHymnalSelection(hymnalId, isSelected);
    }
}
