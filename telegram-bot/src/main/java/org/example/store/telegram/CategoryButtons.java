package org.example.store.telegram;

import org.example.store.products.Product;
import org.example.store.server.ProductFetcher;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryButtons {
    private static final int ITEMS_PER_PAGE = 5;

    // Мапа категорій з підкатегоріями
    public static final Map<String, List<String>> categoryGroups = new LinkedHashMap<>() {{
        put("Молочні продукти", Arrays.asList("Молоко", "Творог", "Сметана", "Йогурт", "Кефір"));
        put("Яйця", Arrays.asList("Яйця"));
        put("Овочі", Arrays.asList("Огірок", "Помідор", "Капуста", "Перець солодкий", "Картопля", "Морква", "Буряк", "Цибуля", "Часник", "Кабачок"));
        put("Фрукти та ягоди", Arrays.asList("Банан", "Яблуко", "Мандарини", "Авокадо", "Лимон", "Апельсин", "Ківі"));
        put("Бакалія", Arrays.asList("Борошно", "Крупа", "Майонез", "Олія", "Цукор", "Сіль"));
        put("Крупа", Arrays.asList("Гречка", "Рис круглий", "Рис довгий", "Вівсянка", "Горох крупа"));
        put("Овочі і фрукти заморожені", Arrays.asList("Капуста броколі", "Суміш овочей"));
        put("Пральні порошки та засоби для прання", Arrays.asList("Пом'якшувач для тканин", "Гель для прання", "Білизна"));
        put("Засоби для прибирання та чищення ", Arrays.asList("Крот"));
        put("Гігієна та догляд", Arrays.asList("Зубні пасти", "Гігієнічні прокладки", "Дезодоранти", "Туалетний папір", "Паперові рушники"));
        put("Ласощі для тварин", Arrays.asList("Ласощі"));
    }};

    public static final Map<String, String> groupToId = Map.ofEntries(
            Map.entry("Молочні продукти", "group_milk"),
            Map.entry("Яйця", "group_eggs"),
            Map.entry("Овочі", "group_ovochi"),
            Map.entry("Фрукти та ягоди", "group_fruits"),
            Map.entry("Бакалія", "group_bakaliya"),
            Map.entry("Крупа", "group_krupa"),
            Map.entry("Овочі і фрукти заморожені", "group_frozen"),
            Map.entry("Пральні порошки та засоби для прання", "group_wash"),
            Map.entry("Засоби для прибирання та чищення ", "group_cleaning"),
            Map.entry("Гігієна та догляд", "group_hygiene"),
            Map.entry("Ласощі для тварин", "group_pets")
    );

    public static final Map<String, String> idToGroup = groupToId.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static final Map<String, String> subcategoryToGroup = categoryGroups.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream().map(sub -> Map.entry(sub, entry.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // ГОЛОВНЕ МЕНЮ КАТЕГОРІЙ
    public static void sendGroupMenu(Long chatId, MyBot bot, int page, Integer messageId) {
        List<String> allGroups = new ArrayList<>(categoryGroups.keySet());
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allGroups.size());

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = start; i < end; i++) {
            String group = allGroups.get(i);
            keyboard.add(Collections.singletonList(createButton(group, groupToId.get(group))));
        }

        List<InlineKeyboardButton> navRow = new ArrayList<>();
        if (page > 0) navRow.add(createButton("⬅️ Назад", "page_" + (page - 1)));
        if (end < allGroups.size()) navRow.add(createButton("Вперед ➡️", "page_" + (page + 1)));
        if (!navRow.isEmpty()) keyboard.add(navRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        String menuText = "Оберіть категорію товару:";

        try {
            if (messageId != null) {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId.toString());
                editMessage.setMessageId(messageId);
                editMessage.setText(menuText);
                editMessage.setReplyMarkup(markup);
                bot.execute(editMessage);
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId.toString());
                sendMessage.setText(menuText);
                sendMessage.setReplyMarkup(markup);
                bot.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    // ПІДКАТЕГОРІЇ
    public static void sendSubcategoryMenu(Long chatId, MyBot bot, String groupName, int page, Integer messageId) {
        List<String> subcategories = categoryGroups.get(groupName);
        if (subcategories == null) return;

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, subcategories.size());

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = start; i < end; i++) {
            String sub = subcategories.get(i);
            String groupId = CategoryButtons.groupToId.get(groupName);
            keyboard.add(Collections.singletonList(createButton(sub, "sub_" + sub + "_" + groupId)));

        }

        List<InlineKeyboardButton> navRow = new ArrayList<>();
        if (page > 0)
            navRow.add(createButton("⬅️ Назад", "subpage_" + groupName + "_" + (page - 1)));
        if (end < subcategories.size())
            navRow.add(createButton("Вперед ➡️", "subpage_" + groupName + "_" + (page + 1)));
        if (!navRow.isEmpty()) keyboard.add(navRow);
        keyboard.add(Collections.singletonList(
                createButton("🔝 Назад до категорій", "main_categories")
        ));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        String menuText = "Оберіть підкатегорію:";

        try {
            if (messageId != null) {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId.toString());
                editMessage.setMessageId(messageId);
                editMessage.setText(menuText);
                editMessage.setReplyMarkup(markup);
                bot.execute(editMessage);
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId.toString());
                sendMessage.setText(menuText);
                sendMessage.setReplyMarkup(markup);
                bot.execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // ПЕРЕЛІК ТОВАРІВ
    public static void sendProductList(Long chatId, MyBot bot, String subcategory, int page, Integer messageId, String groupName) {
        try {
            List<Product> products = ProductFetcher.fetchCheapestProductByCategory(subcategory, groupName);

            if (products == null || products.isEmpty()) {
                bot.sendMessage(chatId, "🔍 Товари в категорії \"" + subcategory + "\" не знайдені.");
                return;
            }

            // Уникнути дублювання товару (з allMatches), якщо перший товар – це "cheapest"
            Set<String> seenTitles = new HashSet<>();
            List<Product> uniqueProducts = new ArrayList<>();

            for (Product p : products) {
                if (!seenTitles.contains(p.getTitle())) {
                    uniqueProducts.add(p);
                    seenTitles.add(p.getTitle());
                }
            }

            // Сортування за ціною
            uniqueProducts.sort(Comparator.comparingDouble(Product::getPrice));

            int start = page * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, uniqueProducts.size());

            StringBuilder text = new StringBuilder("🛒 Товари в категорії: *" + subcategory + "*\n\n");
            for (int i = start; i < end; i++) {
                Product p = uniqueProducts.get(i);
                text.append("🔹 *").append(p.getTitle()).append("*\n")
                        .append("💵 Ціна: ").append(p.getPrice()).append(" грн\n")
                        .append("🏬 Магазин: ").append(p.getStore()).append("\n")
                        .append("🔗 [Перейти до товару](").append(p.getUrl()).append(")\n\n");
            }

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            // Пагінація
            List<InlineKeyboardButton> navRow = new ArrayList<>();
            if (page > 0) {
                navRow.add(createButton("⬅️ Назад", "page_sub_" + subcategory + "_" + (page - 1)));
            }
            if (end < uniqueProducts.size()) {
                navRow.add(createButton("Вперед ➡️", "page_sub_" + subcategory + "_" + (page + 1)));
            }
            if (!navRow.isEmpty()) {
                keyboard.add(navRow);
            }

            // навігаційні кнопки
            keyboard.add(List.of(
                    createButton("◀️ Назад до підкатегорії", CategoryButtons.groupToId.get(groupName)),
                    createButton("🔝 Назад до категорій", "main_categories")
            ));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(keyboard);

            if (messageId != null) {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId.toString());
                editMessage.setMessageId(messageId);
                editMessage.setText(text.toString());
                editMessage.setParseMode("Markdown");
                editMessage.setReplyMarkup(markup);
                bot.execute(editMessage);
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId.toString());
                sendMessage.setText(text.toString());
                sendMessage.setParseMode("Markdown");
                sendMessage.setReplyMarkup(markup);
                bot.execute(sendMessage);
            }

        } catch (IOException | TelegramApiException e) {
            e.printStackTrace();
        }
    }
}