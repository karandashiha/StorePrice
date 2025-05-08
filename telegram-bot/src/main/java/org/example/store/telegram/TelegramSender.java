package org.example.store.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramSender {
    private static final String BOT_TOKEN = "8041550844:AAG9FngzVIwrELJRvDq2vkq0m6NlTVgbJV4";
    private static final String CHAT_ID = "791557451";

    public static void send(String text) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyBot bot = new MyBot(BOT_TOKEN);
            botsApi.registerBot(bot);

            SendMessage message = new SendMessage();
            message.setChatId(CHAT_ID);
            message.setText(text);
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
