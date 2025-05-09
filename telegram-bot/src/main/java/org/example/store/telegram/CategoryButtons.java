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
        button3.setText("Сметана");
        button3.setCallbackData("category_smetana");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(button3);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Йогурти");
        button4.setCallbackData("category_yohurty");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(button4);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Кисломолочні напої");
        button5.setCallbackData("category_kyslomolochni_napoi");
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(button5);

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("Яйця");
        button6.setCallbackData("category_eggs");
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(button6);

        // Створення кнопок для інших категорій
        InlineKeyboardButton button7 = new InlineKeyboardButton();
        button7.setText("Овочі");
        button7.setCallbackData("category_ovochi");
        List<InlineKeyboardButton> row7 = new ArrayList<>();
        row7.add(button7);

        InlineKeyboardButton button8 = new InlineKeyboardButton();
        button8.setText("Фрукти та ягоди");
        button8.setCallbackData("category_frukty_yahody");
        List<InlineKeyboardButton> row8 = new ArrayList<>();
        row8.add(button8);

        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText("Борошно");
        button9.setCallbackData("category_boroshno");
        List<InlineKeyboardButton> row9 = new ArrayList<>();
        row9.add(button9);


        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText("Крупи");
        button10.setCallbackData("category_krupy");
        List<InlineKeyboardButton> row10 = new ArrayList<>();
        row10.add(button10);

        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText("Майонез");
        button11.setCallbackData("category_maionez");
        List<InlineKeyboardButton> row11 = new ArrayList<>();
        row11.add(button11);

        InlineKeyboardButton button12 = new InlineKeyboardButton();
        button12.setText("Соняшникова олія");
        button12.setCallbackData("category_sonyashnykova_oliya");
        List<InlineKeyboardButton> row12 = new ArrayList<>();
        row12.add(button12);

        InlineKeyboardButton button13 = new InlineKeyboardButton();
        button13.setText("Цукор");
        button13.setCallbackData("category_tsukor");
        List<InlineKeyboardButton> row13 = new ArrayList<>();
        row13.add(button13);

        InlineKeyboardButton button14 = new InlineKeyboardButton();
        button14.setText("Сіль");
        button14.setCallbackData("category_sil");
        List<InlineKeyboardButton> row14 = new ArrayList<>();
        row14.add(button14);

        InlineKeyboardButton button15 = new InlineKeyboardButton();
        button15.setText("Овочі і фрукти заморожені");
        button15.setCallbackData("category_ovochi_frukty_zamorozheni");
        List<InlineKeyboardButton> row15 = new ArrayList<>();
        row15.add(button15);

        InlineKeyboardButton button16 = new InlineKeyboardButton();
        button16.setText("Пральні порошки та засоби для прання");
        button16.setCallbackData("category_pralni_zasoby");
        List<InlineKeyboardButton> row16 = new ArrayList<>();
        row16.add(button16);

        InlineKeyboardButton button17 = new InlineKeyboardButton();
        button17.setText("Засоби для прибирання та чищення");
        button17.setCallbackData("category_prybyrannya_chyshchennya");
        List<InlineKeyboardButton> row17 = new ArrayList<>();
        row17.add(button17);

        InlineKeyboardButton button18 = new InlineKeyboardButton();
        button18.setText("Зубні пасти");
        button18.setCallbackData("category_zubni_pasty");
        List<InlineKeyboardButton> row18 = new ArrayList<>();
        row18.add(button18);

        InlineKeyboardButton button19 = new InlineKeyboardButton();
        button19.setText("Гігієнічні прокладки");
        button19.setCallbackData("category_prokladky");
        List<InlineKeyboardButton> row19 = new ArrayList<>();
        row19.add(button19);

        InlineKeyboardButton button20 = new InlineKeyboardButton();
        button20.setText("Дезодоранти");
        button20.setCallbackData("category_dezodoranty");
        List<InlineKeyboardButton> row20 = new ArrayList<>();
        row20.add(button20);

        InlineKeyboardButton button21 = new InlineKeyboardButton();
        button21.setText("Туалетний папір");
        button21.setCallbackData("category_tualetnyy_papir");
        List<InlineKeyboardButton> row21 = new ArrayList<>();
        row21.add(button21);

        InlineKeyboardButton button22 = new InlineKeyboardButton();
        button22.setText("Паперові рушники");
        button22.setCallbackData("category_paperovi_rushnyky");
        List<InlineKeyboardButton> row22 = new ArrayList<>();
        row22.add(button22);

        InlineKeyboardButton button23 = new InlineKeyboardButton();
        button23.setText("Ласощі для тварин");
        button23.setCallbackData("category_lasoschi_dlya_tvaryn");
        List<InlineKeyboardButton> row23 = new ArrayList<>();
        row23.add(button23);

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
        keyboard.add(row12);
        keyboard.add(row13);
        keyboard.add(row14);
        keyboard.add(row15);
        keyboard.add(row16);
        keyboard.add(row17);
        keyboard.add(row18);
        keyboard.add(row19);
        keyboard.add(row20);
        keyboard.add(row21);
        keyboard.add(row22);
        keyboard.add(row23);

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
