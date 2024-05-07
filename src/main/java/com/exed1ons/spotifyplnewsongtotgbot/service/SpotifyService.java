package com.exed1ons.spotifyplnewsongtotgbot.service;

import com.exed1ons.spotifyplnewsongtotgbot.domain.Song;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class SpotifyService {
    private static final Logger logger = LoggerFactory.getLogger(NewSongService.class);

    private final SpotifyTokenRefresher spotifyTokenRefresher;

    public SpotifyService(SpotifyTokenRefresher spotifyTokenRefresher) {
        this.spotifyTokenRefresher = spotifyTokenRefresher;
    }

    public final String SPOTIFY_PLAYLIST_URL = "https://api.spotify.com/v1/playlists/";

    @Getter
    @Setter
    public int totalSongs;

    public int currentTotalSongs = 0;

    public final Set<Song> allSongs = Collections.synchronizedSet(new HashSet<>());

    @Value("${SPOTIFY_TOKEN}")
    private String token;

    private int offset = 0;
    private final int LIMIT = 100;

    private synchronized int getNextOffset() {
        int currentOffset = offset;
        offset += LIMIT;
        return currentOffset;
    }

    private boolean continueParsing = true;


    public boolean sendRequestGetPlaylistItems(String authOptionsUrl, String token) {
        try {
            URL url = new URL(authOptionsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return handlePlaylistTracksResponse(response.toString());
            } else {
                logger.error("Something went wrong with the request");
                return false;
            }

        } catch (IOException e) {
            logger.error("Something went wrong with the request");
            return false;
        }
    }


    public boolean handlePlaylistTracksResponse(String jsonResponse) {
        try {
            JSONObject responseJson = new JSONObject(jsonResponse);
            JSONArray itemsArray = responseJson.getJSONArray("items");
            if (itemsArray.isEmpty()) {
                return false;
            }

            synchronized (allSongs) {
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject item = itemsArray.getJSONObject(i);
                    JSONObject track = item.getJSONObject("track");
                    String addedAt = item.getString("added_at");

                    String trackName = track.getString("name");

                    String url = null;
                    if (track.has("external_urls")) {
                        JSONObject externalUrls = track.getJSONObject("external_urls");
                        if (externalUrls.has("spotify")) {
                            url = externalUrls.getString("spotify");
                        }
                    }

                    JSONArray artistsArray = track.getJSONArray("artists");
                    List<String> listOfArtists = new ArrayList<>();

                    for (int j = 0; j < artistsArray.length(); j++) {
                        JSONObject artist = artistsArray.getJSONObject(j);
                        String artistName = artist.getString("name");
                        listOfArtists.add(artistName);
                    }

                    Song song = new Song(trackName, listOfArtists, addedAt, url);
                    allSongs.add(song);


                    currentTotalSongs++;
                }
            }
        } catch (Exception e) {
            System.out.println("Response contains nullable track");
        }
        logger.info("Current total songs: " + currentTotalSongs);
        return true;
    }


    public List<Song> handleRequest(String playlistId, int totalSongs) {
        setTotalSongs(totalSongs);
        continueParsing = true;
        offset = 0;
        currentTotalSongs = 0;

        token = spotifyTokenRefresher.refreshToken();

        while (continueParsing) {
            int currentOffset = getNextOffset();
            String authOptionsUrl = SPOTIFY_PLAYLIST_URL + playlistId
                    + "/tracks?fields=items%28added_at,track%28name,artists,external_urls%29%29&limit="
                    + LIMIT + "&offset=" + currentOffset;
            logger.info("Sending request to spotify api");

            continueParsing = sendRequestGetPlaylistItems(authOptionsUrl, token);
        }

        int difference = currentTotalSongs - totalSongs;
        logger.info("Difference: " + difference);
        logger.info("Current total songs: " + currentTotalSongs);
        logger.info("Total songs: " + totalSongs);

        setTotalSongs(currentTotalSongs);


        if (difference < 1) {
            return new ArrayList<>();
        }

        return allSongs.stream()
                .sorted(Comparator.comparing(Song::getAddedAt).reversed())
                .limit(difference)
                .collect(Collectors.toList());
    }
}
