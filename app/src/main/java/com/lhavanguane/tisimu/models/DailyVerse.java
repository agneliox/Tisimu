package com.lhavanguane.tisimu.models;

public class DailyVerse {
    private String verse;
    private String reference;
    private String translation;
    private String category;
    private String season;
    private DevotionalContent devotional;
    private String[] themes;
    private String[] tags;
    private String difficulty;

    // For backward compatibility with flat structure
    private String devotionalTitle;
    private String devotionalBody;
    private String application;
    private String reflection;
    private String prayer;

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

    public DevotionalContent getDevotional() { return devotional; }
    public void setDevotional(DevotionalContent devotional) { this.devotional = devotional; }

    public String[] getThemes() { return themes; }
    public void setThemes(String[] themes) { this.themes = themes; }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    // Getters for flat structure (backward compatibility)
    public String getDevotionalTitle() {
        return devotional != null ? devotional.getTitle() : devotionalTitle;
    }
    public void setDevotionalTitle(String title) { this.devotionalTitle = title; }

    public String getDevotionalBody() {
        return devotional != null ? devotional.getBody() : devotionalBody;
    }
    public void setDevotionalBody(String body) { this.devotionalBody = body; }

    public String getApplication() {
        return devotional != null ? devotional.getApplication() : application;
    }
    public void setApplication(String application) { this.application = application; }

    public String getReflection() {
        return devotional != null ? devotional.getReflection() : reflection;
    }
    public void setReflection(String reflection) { this.reflection = reflection; }

    public String getPrayer() {
        return devotional != null ? devotional.getPrayer() : prayer;
    }
    public void setPrayer(String prayer) { this.prayer = prayer; }

    public static class DevotionalContent {
        private String title;
        private String body;
        private String application;
        private String reflection;
        private String prayer;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }

        public String getApplication() { return application; }
        public void setApplication(String application) { this.application = application; }

        public String getReflection() { return reflection; }
        public void setReflection(String reflection) { this.reflection = reflection; }

        public String getPrayer() { return prayer; }
        public void setPrayer(String prayer) { this.prayer = prayer; }
    }
}