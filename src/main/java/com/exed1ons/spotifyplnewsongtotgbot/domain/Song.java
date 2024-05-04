package com.exed1ons.spotifyplnewsongtotgbot.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Song {
    private String name;
    private List<String> artists;
    private String addedAt;
    private String url;

    public Song(String name, List<String> artists, String addedAt, String url) {
        this.name = name;
        this.artists = artists;
        this.addedAt = addedAt;
        this.url = url;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(name, song.name) && Objects.equals(artists, song.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artists);
    }

    @Override
    public String toString() {
        String artistsString = String.join(", ", artists);

        return name + " - " + artistsString;
    }
}
