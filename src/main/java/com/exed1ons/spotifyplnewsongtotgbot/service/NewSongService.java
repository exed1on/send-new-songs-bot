package com.exed1ons.spotifyplnewsongtotgbot.service;

import com.exed1ons.spotifyplnewsongtotgbot.bot.AudioSenderBot;
import com.exed1ons.spotifyplnewsongtotgbot.domain.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.*;

@Service
public class NewSongService {

    private static final Logger logger = LoggerFactory.getLogger(NewSongService.class);

    private final AudioSenderBot audioSenderBot;
    private final SpotifyService spotifyService;
    private final SongDownloadService songDownloadService;
    private final PlaylistToSendService playlistToSendService;

    @Value("${download.directory}")
    private String filePath;

    public NewSongService(AudioSenderBot audioSenderBot, SpotifyService spotifyService, SongDownloadService songDownloadService, PlaylistToSendService playlistToSendService) {
        this.audioSenderBot = audioSenderBot;
        this.spotifyService = spotifyService;
        this.songDownloadService = songDownloadService;
        this.playlistToSendService = playlistToSendService;
    }

    public void getNewSongs() {
        var playlistList = playlistToSendService.getAllPlaylistsToSend();

        for (var playlist : playlistList) {
            List<Song> newSongs = spotifyService.handleRequest(playlist.getPlaylistId(), playlist.getCount());
            playlistToSendService.saveNewCount(playlist.getId(), spotifyService.getTotalSongs());

            for (Song song : newSongs) {
                songDownloadService.downloadSong(song.getUrl());

                String fullPath = filePath + song.getName() + ".mp3";
                sendAudios(fullPath, playlist.getChatId());
                if (songDownloadService.deleteFile(fullPath)) {
                    logger.info("File deleted successfully: " + fullPath);
                } else {
                    logger.warn("Unable to delete file: " + fullPath);
                }
            }
        }

    }

    public void sendAudios(String fullPath, String chatId) {
        logger.info("Sending audio: " + fullPath);
        audioSenderBot.sendAudio(chatId,
                new InputFile(new File(fullPath)));
    }
}
