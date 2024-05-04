package com.exed1ons.spotifyplnewsongtotgbot.bot;

import com.exed1ons.spotifyplnewsongtotgbot.service.NewSongService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Getter
@Component
public class BotInitializer {

    private static final Logger logger = LoggerFactory.getLogger(NewSongService.class);

    private final AudioSenderBot audioSenderBot;

    public BotInitializer(AudioSenderBot audioSenderBot) {
        this.audioSenderBot = audioSenderBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(audioSenderBot);
        } catch (Exception e) {
            logger.error("Error while initializing bot", e);
        }
    }
}
