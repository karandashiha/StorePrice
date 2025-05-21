package org.example.store.products;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String productName;
    private String title;
    private double price;
    private String image;
    private String category;
    private String store;
    private String url;

    @Override
    public String toString() {
        return "🔹 *" + getTitle() + "*\n" +
                "💵 Ціна: " + getPrice() + " грн\n" +
                "🏬 Магазин: " + getStore() + "\n" +
                (getUrl() != null && !getUrl().isEmpty() ? "🔗 [Перейти до товару](" + getUrl() + ")" : "🔗 Посилання відсутнє");
    }

}
