package com.exed1ons.spotifyplnewsongtotgbot.bot;

import com.exed1ons.spotifyplnewsongtotgbot.service.PlaylistToSendService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Setter
@Getter
@Component
public class AudioSenderBot extends TelegramLongPollingBot {

    private final PlaylistToSendService playlistToSendService;

    private String chatId;

    private String botName;

    private String botToken;
    private String playlistId;

    private static final Logger logger = LoggerFactory.getLogger(AudioSenderBot.class);

    public AudioSenderBot(PlaylistToSendService playlistToSendService, @Value("${bot.username}") String botName, @Value("${bot.token}") String botToken) {

        super(botToken);
        this.playlistToSendService = playlistToSendService;
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            if (text != null && text.startsWith("/add")) {
                String[] parts = text.split(" ");
                if (parts.length < 2) {
                    sendMessage(chatId, "Invalid command format");
                    return;
                }
                String playlistUrl = parts[1];
                int count = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;

                var playlistId = extractPlaylistId(playlistUrl);
                playlistToSendService.savePlaylistToSend(playlistId, chatId, count);
            } else if (text != null && text.startsWith("/delete")) {
                String[] parts = text.split(" ");
                if (parts.length < 2) {
                    sendMessage(chatId, "Invalid command format");
                    return;
                }
                String playlistUrl = parts[1];

                var playlistId = extractPlaylistId(playlistUrl);
                playlistToSendService.deletePlaylistToSend(playlistId, chatId);
            }
        }
    }

    private String extractPlaylistId(String spotifyLink) {
        int startIndex = spotifyLink.lastIndexOf("/") + 1;
        int endIndex = spotifyLink.indexOf("?", startIndex);
        if (endIndex == -1) {
            endIndex = spotifyLink.length();
        }
        logger.info("Extracted playlist id: " + spotifyLink.substring(startIndex, endIndex));
        return spotifyLink.substring(startIndex, endIndex);
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error while sending message", e);
        }
    }

    public void sendAudio(String chatId, InputFile audioFile) {
        SendAudio message = new SendAudio();
        message.setChatId(chatId);
        message.setAudio(audioFile);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error while sending message", e);
        }
    }


}
