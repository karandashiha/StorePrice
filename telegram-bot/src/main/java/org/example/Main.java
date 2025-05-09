package org.example;

import org.example.store.telegram.MyBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        // Токен бота та його ініціалізація
        String botToken = "8041550844:AAG9FngzVIwrELJRvDq2vkq0m6NlTVgbJV4";

        // Створюємо об'єкт бота та реєструємо його
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyBot bot = new MyBot(botToken);
            botsApi.registerBot(bot);
            System.out.println("Bot started...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}