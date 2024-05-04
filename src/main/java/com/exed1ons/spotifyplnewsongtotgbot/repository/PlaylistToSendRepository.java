package com.exed1ons.spotifyplnewsongtotgbot.repository;

import com.exed1ons.spotifyplnewsongtotgbot.entity.PlaylistToSend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistToSendRepository extends JpaRepository<PlaylistToSend, Long> {
    Optional<PlaylistToSend> findByPlaylistIdAndChatId(String playlistId, String chatId);
}
