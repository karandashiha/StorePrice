package org.example.store.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

public class CategoryButtons {
    private static final int ITEMS_PER_PAGE = 5;

    private  static final List<String> categories = Arrays.asList(
            "Молоко", "Сир кисломолочний", "Сметана", "Йогурти",
            "Кисломолочні напої", "Яйця", "Овочі", "Фрукти та ягоди",
            "Борошно", "Крупи", "Майонез", "Соняшникова олія",
            "Цукор", "Сіль", "Овочі і фрукти заморожені",
            "Пральні порошки та засоби для прання", "Засоби для прибирання та чищення",
            "Зубні пасти", "Гігієнічні прокладки", "Дезодоранти",
            "Туалетний папір", "Паперові рушники", "Ласощі для тварин"
    );
    public static final Map<String, String> categoryMap = new HashMap<String, String>() {{
        put("Молоко", "milk");
        put("Сир кисломолочний", "syr_kislomolochny");
        put("Сметана", "smetana");
        put("Йогурти", "yohurty");
        put("Кисломолочні напої", "kyslomolochni_napoi");
        put("Яйця", "eggs");
        put("Овочі", "ovochi");
        put("Фрукти та ягоди", "frukty_yahody");
        put("Борошно", "boroshno");
        put("Крупи", "krupy");
        put("Майонез", "maionez");
        put("Соняшникова олія", "sonyashnykova_oliya");
        put("Цукор", "tsukor");
        put("Сіль", "sil");
        put("Овочі і фрукти заморожені", "ovochi_frukty_zamorozheni");
        put("Пральні порошки та засоби для прання", "pralni_zasoby");
        put("Засоби для прибирання та чищення", "prybyrannya_chyshchennya");
        put("Зубні пасти", "zubni_pasty");
        put("Гігієнічні прокладки", "prokladky");
        put("Дезодоранти", "dezodoranty");
        put("Туалетний папір", "tualetnyy_papir");
        put("Паперові рушники", "paperovi_rushnyky");
        put("Ласощі для тварин", "lasoschi_dlya_tvaryn");
    }};
    private static final List<InlineKeyboardButton> allCategoryButtons = categories.stream()
            .map(category -> createButton(category, "category_" +  categoryMap.get(category)))
            .collect(Collectors.toList());

    private static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // Відправляємо пагіновані кнопки
    public static void sendCategoryMenu(Long chatId, MyBot bot, int page, Integer messageId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allCategoryButtons.size());

        // Додаємо кнопки категорій
        for (int i = start; i < end; i++) {
            keyboard.add(Collections.singletonList(allCategoryButtons.get(i)));
        }

        // Кнопки пагінації
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (page > 0) {
            navigationRow.add(createButton("⬅️ Назад", "page_" + (page - 1)));
        }
        if (end < allCategoryButtons.size()) {
            navigationRow.add(createButton("Вперед ➡️", "page_" + (page + 1)));
        }
        if (!navigationRow.isEmpty()) {
            keyboard.add(navigationRow);
        }

        markup.setKeyboard(keyboard);
        if (messageId != null) {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId.toString());
            editMessage.setMessageId(messageId);
            editMessage.setText("Оберіть категорію товару:");
            editMessage.setReplyMarkup(markup);

            try {
                bot.execute(editMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText("Оберіть категорію товару:");
            sendMessage.setReplyMarkup(markup);

            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Для команди /menu - відображаємо першу сторінку
    public static void sendCategoryMenu(long chatId, MyBot bot) {
        sendCategoryMenu(chatId, bot, 0, null); // Оскільки це перше повідомлення, не передаємо messageId
    }
}
