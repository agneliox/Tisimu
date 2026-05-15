package com.lhavanguane.tisimu.models;

import java.util.List;

public class HymnalManifest {
    private int version;
    private String lastUpdated;
    private List<HymnalInfo> hymnals;


    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<HymnalInfo> getHymnals() { return hymnals; }
    public void setHymnals(List<HymnalInfo> hymnals) { this.hymnals = hymnals; }

    public static class HymnalInfo {
        private String id;
        private String name;
        private String description;
        private String language;
        private int totalSongs;
        private String fileUrl;
        private String coverUrl;
        private long fileSize;
        private int version;
        private String author;
        private boolean isDownloaded;
        private boolean needsUpdate;

        private boolean isSelected;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public int getTotalSongs() { return totalSongs; }
        public void setTotalSongs(int totalSongs) { this.totalSongs = totalSongs; }

        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public boolean isDownloaded() { return isDownloaded; }
        public void setDownloaded(boolean downloaded) { isDownloaded = downloaded; }

        public boolean isNeedsUpdate() { return needsUpdate; }
        public void setNeedsUpdate(boolean needsUpdate) { this.needsUpdate = needsUpdate; }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HymnalInfo that = (HymnalInfo) o;
            return id != null ? id.equals(that.id) : that.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}
