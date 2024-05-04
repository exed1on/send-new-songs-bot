package com.exed1ons.spotifyplnewsongtotgbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SpotifyTokenRefresher {

    private static final Logger logger = LoggerFactory.getLogger(NewSongService.class);

    public final String UNSUCCESSFULLY_REFRESHED_TOKEN = "UNSUCCESSFULLY REFRESHED TOKEN";
    @Value("${SPOTIFY_CLIENT_ID}")
    public String CLIENT_ID;
    @Value("${SPOTIFY_CLIENT_SECRET}")
    public String CLIENT_SECRET;
    @Value("${SPOTIFY_REFRESH_TOKEN}")
    public String REFRESH_TOKEN;

    public String refreshToken() {

        try {
            String base64Credentials = java.util.Base64.getEncoder()
                    .encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
            String authOptionsUrl = "https://accounts.spotify.com/api/token";
            String authOptionsBody = "grant_type=refresh_token&refresh_token=" + REFRESH_TOKEN;

            URL url = new URL(authOptionsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + base64Credentials);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            connection.getOutputStream().write(authOptionsBody.getBytes());

            int responseCode = connection.getResponseCode();
            System.out.println("Refresh Token Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                String jsonResponse = response.toString();

                String token = jsonResponse.split("\"access_token\"\\s*:\\s*\"")[1].split("\"")[0];

                logger.info("Token refreshed successfully. New token: " + token);
                return token;
            } else {
                logger.error("Failed to refresh token. Status: " + responseCode);
            }

        } catch (IOException e) {
            logger.error("Failed to refresh token");
        }
        throw new RuntimeException(UNSUCCESSFULLY_REFRESHED_TOKEN);
    }
}
