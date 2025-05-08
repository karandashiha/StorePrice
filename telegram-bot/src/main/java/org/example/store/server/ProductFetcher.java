package org.example.store.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.store.products.Product;

import java.io.IOException;
import java.util.List;

public class ProductFetcher {

    // Метод для отримання товарів за категорією
    public static List<Product> fetchProductsByCategory(String category) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://localhost:3000/products/" + category;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<List<Product>>() {
            }.getType());
        }
    }

    // Метод для отримання найкращої ціни для продукту в категорії
    public static Product fetchCheapestProduct(String productName, String category) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = "http://localhost:3000/product?productName=" + productName + "&category=" + category;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, Product.class);
        }
    }
}
