package com.lhavanguane.tisimu.models;

import java.util.List;

public class HymnalData {
    private String id;
    private String name;
    private int version;
    private Metadata metadata;
    private List<Section> sections;
    private List<Song> songs;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }

    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }

    public static class Metadata {
        private String description;
        private String language;
        private int totalSongs;
        private int year;
        private String publisher;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public int getTotalSongs() { return totalSongs; }
        public void setTotalSongs(int totalSongs) { this.totalSongs = totalSongs; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
    }

    public static class Section {
        private int id;
        private int sequence;
        private String name;
        private int startNumber;
        private int endNumber;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getSequence() { return sequence; }
        public void setSequence(int sequence) { this.sequence = sequence; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getStartNumber() { return startNumber; }
        public void setStartNumber(int startNumber) { this.startNumber = startNumber; }

        public int getEndNumber() { return endNumber; }
        public void setEndNumber(int endNumber) { this.endNumber = endNumber; }
    }

    public static class Song {
        private int number;
        private String title;
        private String lyrics;
        private String author;
        private String composer;
        private String key;
        private String timeSignature;

        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getLyrics() { return lyrics; }
        public void setLyrics(String lyrics) { this.lyrics = lyrics; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getComposer() { return composer; }
        public void setComposer(String composer) { this.composer = composer; }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public String getTimeSignature() { return timeSignature; }
        public void setTimeSignature(String timeSignature) { this.timeSignature = timeSignature; }
    }
}