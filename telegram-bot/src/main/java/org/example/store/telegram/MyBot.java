package org.example.store.telegram;

import org.example.store.products.Product;
import org.example.store.server.ProductFetcher;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.store.telegram.CategoryButtons.categoryMap;


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
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            // Логування callbackData для відлагодження
            System.out.println("Received callback: " + callbackData);

            // Обробка пагінації категорій
            if (callbackData.startsWith("page_")) {
                int page = Integer.parseInt(callbackData.split("_")[1]);
                CategoryButtons.sendCategoryMenu(chatId, this, page, messageId); // ✅ це EditMessage
                return;
            }
            // Обробка вибору категорії товарів
            if (callbackData.startsWith("category_")) {
                String categoryKey = callbackData.substring("category_".length());

                // Перетворюємо короткий код назад на повну назву категорії
                String categoryName = categoryMap.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(categoryKey))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("Невідома категорія");
                try {
                    System.out.println("Fetching products for category: " + categoryName);
                    List<Product> products = ProductFetcher.fetchProductsByCategory(categoryName);

                    if (products != null && !products.isEmpty()) {
                        StringBuilder productsList = new StringBuilder("Товари в категорії " + categoryName + ":\n");
                        for (Product product : products) {
                            productsList.append(product.getProductName())
                                    .append(" - ").append(product.getPrice()).append(" грн\n");
                        }
                        sendMessage(chatId, productsList.toString());
                    } else {
                        sendMessage(chatId, "Товари в цій категорії не знайдені.");
                    }
                } catch (IOException e) {
                    sendMessage(chatId, "Сталася помилка при отриманні товарів для цієї категорії.");
                    e.printStackTrace();
                }
            }
        }
    }

    // Обробка категорій товарів
    private static final Map<String, String> categoryResponses = new HashMap<>();

    static {
        categoryResponses.put("category_milk", "Оберіть товар в категорії Молоко.");
        categoryResponses.put("category_syr_kislomolochny", "Оберіть товар в категорії Сир кисломолочний.");
        categoryResponses.put("category_smetana", "Оберіть товар в категорії Сметана.");
        categoryResponses.put("category_yohurty", "Оберіть товар в категорії Йогурти.");
        categoryResponses.put("category_kyslomolochni_napoi", "Оберіть товар в категорії Кисломолочні напої.");
        categoryResponses.put("category_eggs", "Оберіть товар в категорії Яйця.");
        categoryResponses.put("category_ovochi", "Оберіть товар в категорії Овочі.");
        categoryResponses.put("category_frukty_yahody", "Оберіть товар в категорії Фрукти та ягоди.");
        categoryResponses.put("category_boroshno", "Оберіть товар в категорії Борошно.");
        categoryResponses.put("category_krupy", "Оберіть товар в категорії Крупи.");
        categoryResponses.put("category_maionez", "Оберіть товар в категорії Майонез.");
        categoryResponses.put("category_sonyashnykova_oliya", "Оберіть товар в категорії Соняшникова олія.");
        categoryResponses.put("category_tsukor", "Оберіть товар в категорії Цукор.");
        categoryResponses.put("category_sil", "Оберіть товар в категорії Сіль.");
        categoryResponses.put("category_ovochi_frukty_zamorozheni", "Оберіть товар в категорії Заморожені овочі та фрукти.");
        categoryResponses.put("category_pralni_zasoby", "Оберіть товар в категорії Засоби для прання.");
        categoryResponses.put("category_prybyrannya_chyshchennya", "Оберіть товар в категорії Засоби для прибирання.");
        categoryResponses.put("category_zubni_pasty", "Оберіть товар в категорії Зубні пасти.");
        categoryResponses.put("category_prokladky", "Оберіть товар в категорії Гігієнічні прокладки.");
        categoryResponses.put("category_dezodoranty", "Оберіть товар в категорії Дезодоранти.");
        categoryResponses.put("category_tualetnyy_papir", "Оберіть товар в категорії Туалетний папір.");
        categoryResponses.put("category_paperovi_rushnyky", "Оберіть товар в категорії Паперові рушники.");
        categoryResponses.put("category_lasoschi_dlya_tvaryn", "Оберіть товар в категорії Ласощі для тварин.");
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
