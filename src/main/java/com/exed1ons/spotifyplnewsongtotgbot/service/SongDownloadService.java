package com.exed1ons.spotifyplnewsongtotgbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SongDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(NewSongService.class);


    @Value("${download.directory}")
    private String downloadDirectory;

    public void downloadSong(String url) {
        try {
            File directory = new File(downloadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
                logger.info("Download directory created: " + downloadDirectory);
            }

            String[] command = {"spotdl", "--audio", "youtube", "--lyrics", "genius", "--output", "{title}", url};

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(downloadDirectory));
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Download completed successfully.");
            } else {
                System.err.println("Download failed. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing spotdl command: " + e.getMessage());
        }
    }

    public boolean deleteFile(String filePath) {
        if (filePath != null) {
            File fileToDelete = new File(filePath);
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    System.out.println("File deleted successfully: " + filePath);
                    return true;
                } else {
                    System.err.println("Unable to delete file: " + filePath);
                    return false;
                }
            }
        }
        return false;
    }
}
