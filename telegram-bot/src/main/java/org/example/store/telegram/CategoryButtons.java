package org.example.store.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class CategoryButtons {
    public static void sendCategoryMenu(long chatId, MyBot bot) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Створення кнопок для молочних продуктів
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Молоко");
        button1.setCallbackData("category_milk");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Сир кисломолочний");
        button2.setCallbackData("category_syr_kislomolochny");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Кисломолочні сири");
        button3.setCallbackData("category_syr_kislomolochny");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(button3);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Сметана");
        button4.setCallbackData("category_smetana");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(button4);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Йогурти");
        button5.setCallbackData("category_yohurty");
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(button5);

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("Кисломолочні напої");
        button6.setCallbackData("category_kyslomolochni_napoi");
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(button6);

        InlineKeyboardButton button7 = new InlineKeyboardButton();
        button7.setText("Кефір");
        button7.setCallbackData("category_kefir");
        List<InlineKeyboardButton> row7 = new ArrayList<>();
        row7.add(button7);

        InlineKeyboardButton button8 = new InlineKeyboardButton();
        button8.setText("Яйця");
        button8.setCallbackData("category_eggs");
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        row8.add(button8);

        // Створення кнопок для інших категорій
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText("Овочі");
        button9.setCallbackData("category_ovochi");
        List<InlineKeyboardButton> row9 = new ArrayList<>();
        row9.add(button9);

        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText("Фрукти та ягоди");
        button10.setCallbackData("category_frukty_yahody");
        List<InlineKeyboardButton> row10 = new ArrayList<>();
        row10.add(button10);

        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText("Борошно та крупи");
        button11.setCallbackData("category_boroshno_krupy");
        List<InlineKeyboardButton> row11 = new ArrayList<>();
        row11.add(button11);

        // Додавання кнопок до клавіатури
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        keyboard.add(row6);
        keyboard.add(row7);
        keyboard.add(row8);
        keyboard.add(row9);
        keyboard.add(row10);
        keyboard.add(row11);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Оберіть категорію товару:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
