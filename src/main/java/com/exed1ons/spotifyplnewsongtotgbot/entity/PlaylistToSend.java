package com.exed1ons.spotifyplnewsongtotgbot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PlaylistToSend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String playlistId;
    private String chatId;
    private int count;
}
