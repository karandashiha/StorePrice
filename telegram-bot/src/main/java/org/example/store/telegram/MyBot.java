package org.example.store.telegram;

import org.example.store.products.Product;
import org.example.store.server.ProductFetcher;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MyBot extends TelegramLongPollingBot {

    private final String token;

    // Тут зберігаємо, чи юзер зараз у режимі пошуку товару
    private final Map<Long, Boolean> userSearchMode = new ConcurrentHashMap<>();

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
        if (update.hasCallbackQuery()) {
            CallbackHandler.handle(update, this);
            return;
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText();
            Long chatId = message.getChatId();

            switch (text) {
                case "/start":
                    userSearchMode.put(chatId, false);  // скидаємо статус пошуку
                    sendMessageWithKeyboard(chatId,
                            "Привіт! Я бот для порівняння цін на товари.\n\n" +
                                    "Що вміє цей бот:\n" +
                                    "1. Порівнює ціни на товари з різних магазинів.\n" +
                                    "2. Допомагає знайти найнижчу ціну на товар у різних магазинах.\n\n" +
                                    "Як це працює:\n" +
                                    "1. Запусти бота /start\n" +
                                    "2. Обери категорію товару.\n" +
                                    "3. Знайди товар і дізнайся, де він найдешевший.");
                    break;

                case "🛍 Огляд категорій":
                    userSearchMode.put(chatId, false);
                    CategoryButtons.sendGroupMenu(chatId, this, 0, null);
                    break;

                case "🔍 Знайти найдешевший товар":
                    userSearchMode.put(chatId, true);
                    sendMessage(chatId, "Введіть назву товару (наприклад: \"молоко\"). Для виходу введіть 'Вийти'.");
                    break;

                default:
                    boolean inSearchMode = userSearchMode.getOrDefault(chatId, false);
                    if (inSearchMode) {
                        if (text.equalsIgnoreCase("вийти")) {
                            userSearchMode.put(chatId, false);
                            sendMessageWithKeyboard(chatId, "Ви вийшли з режиму пошуку.\nЩоб почати знову, натисніть 🔍 Знайти найдешевший товар.");
                        } else {
                            try {
                                Product product = ProductFetcher.fetchCheapestProduct(text.trim());
                                sendMessageWithKeyboard(chatId, "Найдешевший товар:\n" + product.toString());
                            } catch (IOException e) {
                                sendMessageWithKeyboard(chatId, "Помилка при отриманні товару.");
                            }
                        }
                    }
                    break;
            }
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("🛍 Огляд категорій");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("🔍 Знайти найдешевший товар");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
