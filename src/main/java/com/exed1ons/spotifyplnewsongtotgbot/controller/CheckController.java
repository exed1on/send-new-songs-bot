package com.exed1ons.spotifyplnewsongtotgbot.controller;

import com.exed1ons.spotifyplnewsongtotgbot.service.NewSongService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {

    private final NewSongService newSongService;

    public CheckController(NewSongService newSongService) {
        this.newSongService = newSongService;
    }

    @GetMapping("/")
    public String checkForUpdates() {
        newSongService.getNewSongs();
        return "check for updates";
    }
}
