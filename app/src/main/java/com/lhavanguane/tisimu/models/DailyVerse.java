package com.lhavanguane.tisimu.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DailyVerse implements Serializable {
    private String id;
    private String verse;
    private String reference;
    private String translation;
    private String category;
    private String season;
    private String devotionalTitle;
    private String devotionalBody;
    private String application;
    private String reflection;
    private String prayer;
    private List<String> themes;
    private List<String> tags;
    private String difficulty;
    private String language;
    private String date;
    @ServerTimestamp
    private Date updatedAt;

    public DailyVerse() {

    }

    // Getters and Setters
    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVerse() { return verse; }
    public void setVerse(String verse) { this.verse = verse; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public String getDevotionalTitle() { return devotionalTitle; }
    public void setDevotionalTitle(String devotionalTitle) { this.devotionalTitle = devotionalTitle; }

    public String getDevotionalBody() { return devotionalBody; }
    public void setDevotionalBody(String devotionalBody) { this.devotionalBody = devotionalBody; }

    public String getApplication() { return application; }
    public void setApplication(String application) { this.application = application; }

    public String getReflection() { return reflection; }
    public void setReflection(String reflection) { this.reflection = reflection; }

    public String getPrayer() { return prayer; }
    public void setPrayer(String prayer) { this.prayer = prayer; }

    public List<String> getThemes() { return themes; }
    public void setThemes(List<String> themes) { this.themes = themes; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}