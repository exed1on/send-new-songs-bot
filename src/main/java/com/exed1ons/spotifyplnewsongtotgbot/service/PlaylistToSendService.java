package com.exed1ons.spotifyplnewsongtotgbot.service;

import com.exed1ons.spotifyplnewsongtotgbot.entity.PlaylistToSend;
import com.exed1ons.spotifyplnewsongtotgbot.repository.PlaylistToSendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistToSendService {

    private static final Logger logger = LoggerFactory.getLogger(NewSongService.class);

    private final PlaylistToSendRepository playlistToSendRepository;

    public PlaylistToSendService(PlaylistToSendRepository playlistToSendRepository) {
        this.playlistToSendRepository = playlistToSendRepository;
    }

    public void savePlaylistToSend(String playlistId, String chatId, int count) {
        var playlistToSend = PlaylistToSend.builder()
                .playlistId(playlistId)
                .chatId(chatId)
                .count(count)
                .build();

        playlistToSendRepository.save(playlistToSend);
    }

    public List<PlaylistToSend> getAllPlaylistsToSend() {
        return playlistToSendRepository.findAll();
    }

    public void saveNewCount(long id, int count) {
        logger.info("Saving new count: " + count);
        var playlistToSend = playlistToSendRepository.findById(id).orElseThrow();
        playlistToSend.setCount(count);
        playlistToSendRepository.save(playlistToSend);
    }

    public void deletePlaylistToSend(String playlistId, String chatId) {
        var playlist = playlistToSendRepository.findByPlaylistIdAndChatId(playlistId, chatId);
        if (playlist.isPresent()) {
            playlistToSendRepository.delete(playlist.get());
            logger.info("Playlist with ID {} and chat ID {} deleted successfully.", playlistId, chatId);
        } else {
            logger.warn("Playlist with ID {} and chat ID {} not found for deletion.", playlistId, chatId);
        }
    }
}
