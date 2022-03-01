package com.example.hst.top10downloader;

/**
 * Created by hst on 12/11/2017.
 */

public class FeedEntry {
    private String name;
    private String artist;
    private String releaseDate;
    private String genre;
    private String imageURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return  "name=" + name + '\n' +
                ", artist=" + artist + '\n' +
                ", releaseDate=" + releaseDate + '\n' +
                ", genre=" + genre + '\n' +
                ", imageURL=" + imageURL + '\n';
    }
}
