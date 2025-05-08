package org.example.store.telegram;

import org.example.store.products.Product;
import org.example.store.server.ProductFetcher;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class MyBot extends TelegramLongPollingBot {

    private final String token;

    public MyBot(String token) {
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return "atb_varus_silpo_price_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText();

            if (text.equals("/start")) {
                sendMessage(message.getChatId(), "Привіт! Я бот для порівняння цін на товари.\n\n" +
                        "Що вміє цей бот:\n" +
                        "1. Порівнює ціни на товари з різних магазинів.\n" +
                        "2. Допомагає знайти найнижчу ціну на товар у різних магазинах.\n\n" +
                        "Як це працює:\n" +
                        "1. Запусти бота /start\n" +
                        "2. Обери категорію товару.\n" +
                        "3. Знайди товар і дізнайся, де він найдешевший.\n\n" +
                        "Спробуй вибрати категорію товару /menu");
            }

            if (text.equals("/menu")) {
                CategoryButtons.sendCategoryMenu(message.getChatId(), this); // Викликаємо метод із CategoryButtons
            }

            if (text.startsWith("/cheapest")) {
                String[] parts = text.split(" ");
                if (parts.length == 3) {
                    String productName = parts[1];
                    String category = parts[2];

                    try {
                        Product cheapestProduct = ProductFetcher.fetchCheapestProduct(productName, category);
                        sendMessage(message.getChatId(), "Найдешевший товар: \n" + cheapestProduct.toString());
                    } catch (IOException e) {
                        sendMessage(message.getChatId(), "Сталася помилка при отриманні даних.");
                    }
                } else {
                    sendMessage(message.getChatId(), "Вкажіть назву товару і категорію для порівняння цін.");
                }
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            // Обробка натискання на кнопки категорій
            if (callbackData.equals("category_milk")) {
                sendMessage(chatId, "Оберіть товар в категорії Молочні продукти.");
            } else if (callbackData.equals("category_vegetables")) {
                sendMessage(chatId, "Оберіть товар в категорії Овочі.");
            } else if (callbackData.equals("category_syr_kislomolochny")) {
                sendMessage(chatId, "Оберіть товар в категорії Сир кисломолочний.");
            }
            // Додати інші обробки для інших категорій...
        }
    }


    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
