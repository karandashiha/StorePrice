package org.example.store.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
public class CallbackHandler {

    public static void handle(Update update, MyBot bot) {
        if (!update.hasCallbackQuery()) return;

        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        System.out.println("Received callback: " + data);

        if (data.equals("main_categories")) {
            CategoryButtons.sendGroupMenu(chatId, bot, 0, messageId);
        } else if (data.startsWith("page_") && !data.startsWith("page_sub_")) {
            handleCategoryPage(data, chatId, bot, messageId);
        } else if (data.startsWith("group_")) {
            handleGroupSelection(data, chatId, bot, messageId);
        } else if (data.startsWith("subpage_")) {
            handleSubcategoryPagination(data, chatId, bot, messageId);
        } else if (data.startsWith("sub_")) {
            handleSubcategorySelection(data, chatId, bot, messageId);
        } else if (data.startsWith("page_sub_")) {
            handleProductPagination(data, chatId, bot, messageId);
        } else {
            bot.sendMessage(chatId, "⚠️ Невідомий тип колбеку: " + data);
        }
    }

    private static void handleCategoryPage(String data, Long chatId, MyBot bot, int messageId) {
        int page = parseIntSafe(data.substring("page_".length()), 0);
        CategoryButtons.sendGroupMenu(chatId, bot, page, messageId);
    }

    private static void handleGroupSelection(String data, Long chatId, MyBot bot, int messageId) {
        String groupName = CategoryButtons.idToGroup.get(data);
        if (groupName != null) {
            CategoryButtons.sendSubcategoryMenu(chatId, bot, groupName, 0, messageId);
        } else {
            bot.sendMessage(chatId, "Категорія не знайдена.");
        }
    }

    private static void handleSubcategoryPagination(String data, Long chatId, MyBot bot, int messageId) {
        String[] parts = data.substring("subpage_".length()).split("_");
        if (parts.length < 2) {
            bot.sendMessage(chatId, "Не вдалося обробити запит на пагінацію.");
            return;
        }

        int page = parseIntSafe(parts[parts.length - 1], 0);
        String groupName = String.join("_", Arrays.copyOf(parts, parts.length - 1));

        CategoryButtons.sendSubcategoryMenu(chatId, bot, groupName, page, messageId);
    }

    private static void handleSubcategorySelection(String data, Long chatId, MyBot bot, int messageId) {
        String[] parts = data.substring("sub_".length()).split("_group_");
        if (parts.length != 2) {
            bot.sendMessage(chatId, "⚠️ Невірний формат підкатегорії.");
            return;
        }

        String subcategory = parts[0];
        String groupId = "group_" + parts[1];
        String groupName = CategoryButtons.idToGroup.get(groupId);

        if (groupName == null) {
            bot.sendMessage(chatId, "⚠️ Категорію не знайдено для підкатегорії.");
            return;
        }

        CategoryButtons.sendProductList(chatId, bot, subcategory, 0, messageId, groupName);
    }

    private static void handleProductPagination(String data, Long chatId, MyBot bot, int messageId) {
        String[] parts = data.substring("page_sub_".length()).split("_");
        if (parts.length < 2) {
            bot.sendMessage(chatId, "⚠️ Невірний формат пагінації товарів.");
            return;
        }

        int page = parseIntSafe(parts[parts.length - 1], 0);
        String subcategory = String.join("_", Arrays.copyOf(parts, parts.length - 1));

        String groupName = CategoryButtons.subcategoryToGroup.get(subcategory);

        if (groupName == null) {
            bot.sendMessage(chatId, "⚠️ Категорія не знайдена для підкатегорії \"" + subcategory + "\".");
            return;
        }

        CategoryButtons.sendProductList(chatId, bot, subcategory, page, messageId, groupName);
    }

    private static int parseIntSafe(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
