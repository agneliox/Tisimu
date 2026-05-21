package com.lhavanguane.tisimu.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lhavanguane.tisimu.entities.Hymnal;

import java.util.List;

public class HymnalViewModel extends AndroidViewModel {
    private final HymnalRepository repository;
    private final LiveData<List<Hymnal>> allHymnals;
    private final LiveData<List<Hymnal>> selectedHymnals;
    private final LiveData<Integer> selectedCount;
    public HymnalViewModel(@NonNull Application application) {
        super(application);
        repository = new HymnalRepository(application);
        allHymnals = repository.getAllHymnals();
        selectedHymnals = repository.getSelectedHymnals();
        selectedCount = repository.getSelectedCount();
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
