package org.example.store.products;

public class Product {
    String productName;
    String title;
    double price;
    String image;
    String category;

    // Конструктор для ініціалізації
    public Product(String productName, String title, double price, String image, String category) {
        this.productName = productName;
        this.title = title;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("🛒 %s\n📦 %s\n💸 %s грн\n📂 Категорія: %s\n",
                productName, title, price, category);

    }
}
