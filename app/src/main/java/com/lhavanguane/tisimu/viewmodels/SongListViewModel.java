package com.lhavanguane.tisimu.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lhavanguane.tisimu.entities.Section;
import com.lhavanguane.tisimu.entities.Song;
import com.lhavanguane.tisimu.models.SectionWithSongs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongListViewModel extends AndroidViewModel {
    private final HymnalRepository hymnalRepository;
    private final MutableLiveData<List<SectionWithSongs>> sectionWithSongsLiveData;
    private final MutableLiveData<List<SectionWithSongs>> filteredSectionWithSongsLiveData;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> searchQuery;

    private final List<Integer> selectedHymnalIds;
    private final List<Section> allSections;
    private final List<Song> allSongs;
    private boolean isSectionsLoaded;
    private boolean isSongsLoaded;

    public SongListViewModel(Application application) {
        super(application);
        hymnalRepository = new HymnalRepository(application);
        sectionWithSongsLiveData = new MutableLiveData<>();
        filteredSectionWithSongsLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(true);
        searchQuery = new MutableLiveData<>("");

        selectedHymnalIds = new ArrayList<>();
        allSections = new ArrayList<>();
        allSongs = new ArrayList<>();
        isSectionsLoaded = false;
        isSongsLoaded = false;

        loadData();
    }

    private void loadData() {
        isLoading.setValue(true);

        // Reset flags
        isSectionsLoaded = false;
        isSongsLoaded = false;
        allSections.clear();
        allSongs.clear();

        // Load selected hymnal IDs
        hymnalRepository.getSelectedHymnalIds().observeForever(ids -> {
            if (ids != null && !ids.isEmpty()) {
                selectedHymnalIds.clear();
                selectedHymnalIds.addAll(ids);

                // Load sections and songs
                loadSections();
                loadSongs();
            } else {
                isLoading.setValue(false);
                sectionWithSongsLiveData.setValue(new ArrayList<>());
                filteredSectionWithSongsLiveData.setValue(new ArrayList<>());
            }
        });
    }

    private void loadSections() {
        if (selectedHymnalIds.isEmpty()) return;

        hymnalRepository.getSectionsForSelectedHymnals(selectedHymnalIds).observeForever(sections -> {
            if (sections != null) {
                allSections.clear();
                allSections.addAll(sections);
                isSectionsLoaded = true;
                tryCombineData();
            }
        });
    }

    private void loadSongs() {
        if (selectedHymnalIds.isEmpty()) return;

        hymnalRepository.getSongsForSelectedHymnals(selectedHymnalIds).observeForever(songs -> {
            if (songs != null) {
                allSongs.clear();
                allSongs.addAll(songs);
                isSongsLoaded = true;
                tryCombineData();
            }
        });
    }

    private void tryCombineData() {
        if (isSectionsLoaded && isSongsLoaded) {
            combineData();
        }
    }

    private void combineData() {
        if (allSections.isEmpty() || allSongs.isEmpty()) {
            sectionWithSongsLiveData.setValue(new ArrayList<>());
            filteredSectionWithSongsLiveData.setValue(new ArrayList<>());
            isLoading.setValue(false);
            return;
        }

        // Group songs by section
        Map<Integer, List<Song>> songsBySection = new HashMap<>();
        for (Song song : allSongs) {
            int sectionId = song.getSectionId();
            if (!songsBySection.containsKey(sectionId)) {
                songsBySection.put(sectionId, new ArrayList<>());
            }
            songsBySection.get(sectionId).add(song);
        }

        List<SectionWithSongs> sectionWithSongsList = new ArrayList<>();
        for (Section section : allSections) {
            List<Song> songsInSection = songsBySection.get(section.getId());
            if (songsInSection != null && !songsInSection.isEmpty()) {
                sectionWithSongsList.add(new SectionWithSongs(section, songsInSection));
            }
        }

        sectionWithSongsLiveData.setValue(sectionWithSongsList);
        applyFilter();
        isLoading.setValue(false);
    }

    public void searchSongs(String query) {
        searchQuery.setValue(query);
        applyFilter();
    }

    private void applyFilter() {
        List<SectionWithSongs> original = sectionWithSongsLiveData.getValue();
        String query = searchQuery.getValue();

        if (original == null) {
            filteredSectionWithSongsLiveData.setValue(new ArrayList<>());
            return;
        }

        if (query == null || query.trim().isEmpty()) {
            filteredSectionWithSongsLiveData.setValue(original);
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();
        List<SectionWithSongs> filtered = new ArrayList<>();

        for (SectionWithSongs sectionWithSongs : original) {
            List<Song> filteredSongs = new ArrayList<>();
            for (Song song : sectionWithSongs.getSongs()) {
                if (song.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(song.getNumber()).contains(lowerCaseQuery) ||
                        (song.getLyrics() != null && song.getLyrics().toLowerCase().contains(lowerCaseQuery))) {
                    filteredSongs.add(song);
                }
            }

            if (!filteredSongs.isEmpty()) {
                SectionWithSongs newSection = new SectionWithSongs(
                        sectionWithSongs.getSection(),
                        filteredSongs
                );
                newSection.setExpanded(sectionWithSongs.isExpanded());
                filtered.add(newSection);
            }
        }

        filteredSectionWithSongsLiveData.setValue(filtered);
    }

    public void toggleSectionExpansion(int position) {
        List<SectionWithSongs> current = filteredSectionWithSongsLiveData.getValue();
        if (current != null && position < current.size()) {
            SectionWithSongs section = current.get(position);
            section.setExpanded(!section.isExpanded());
            filteredSectionWithSongsLiveData.setValue(current);
        }
    }

    public LiveData<List<SectionWithSongs>> getSectionWithSongs() {
        return filteredSectionWithSongsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void refresh() {
        loadData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observers if needed
    }
}
