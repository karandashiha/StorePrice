package org.example.store.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.store.products.Product;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProductFetcher {
    // Метод для отримання товарів за категорією
    public static List<Product> fetchCheapestProductByCategory(String productName, String category) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://localhost:3000/product_category?productName=" +
                URLEncoder.encode(productName, StandardCharsets.UTF_8) +
                "&category=" + URLEncoder.encode(category, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error: " + response.code() + " - " + response.message());
            }

            String json = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            List<Product> productList = new ArrayList<>();

            // Опрацьовуємо allMatches
            if (jsonObject.has("allMatches") && jsonObject.get("allMatches").isJsonArray()) {
                for (JsonElement element : jsonObject.get("allMatches").getAsJsonArray()) {
                    JsonObject obj = element.getAsJsonObject();
                    Product product = new Product();
                    product.setProductName(obj.has("productName") ? obj.get("productName").getAsString() : "Невідомо");
                    product.setTitle(obj.has("title") ? obj.get("title").getAsString() : "Без назви");
                    product.setPrice(obj.has("price") ? obj.get("price").getAsDouble() : 0.0);
                    product.setStore(obj.has("store") ? obj.get("store").getAsString() : "Невідомий магазин");
                    product.setUrl(obj.has("url") ? obj.get("url").getAsString() : "");
                    productList.add(product);
                }
            }
            return productList;
        }
    }


    // Метод для отримання найкращої ціни для продукту
    public static Product fetchCheapestProduct(String productName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://localhost:3000/product?productName=" + URLEncoder.encode(productName, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error: " + response.code() + " - " + response.message());
            }

            String json = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            // Перевіряємо наявність поля "cheapest"
            if (!jsonObject.has("cheapest") || jsonObject.get("cheapest").isJsonNull()) {
                throw new IOException("Відповідь не містить 'cheapest' товар.");
            }

            JsonObject cheapest = jsonObject.getAsJsonObject("cheapest");

            Product product = new Product();
            product.setProductName(cheapest.has("productName") ? cheapest.get("productName").getAsString() : "Невідомо");
            product.setTitle(cheapest.has("title") ? cheapest.get("title").getAsString() : "Без назви");
            product.setPrice(cheapest.has("price") ? cheapest.get("price").getAsDouble() : 0.0);
            product.setStore(cheapest.has("store") ? cheapest.get("store").getAsString() : "Невідомий магазин");
            product.setUrl(cheapest.has("url") ? cheapest.get("url").getAsString() : "");

            return product;
        }
    }

}
