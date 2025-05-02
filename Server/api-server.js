const express = require("express");
const cors = require("cors");
const {
  getProductsByCategory,
  findCheapestProductByProductNameAndCategory,
} = require("./utils");

const app = express();
const PORT = 3000;

app.use(cors());

// Отримати всі товари певної категорії з усіх магазинів
app.get("/products/:category", (req, res) => {
  const category = req.params.category;
  const results = getProductsByCategory(category);
  res.json(results);
});

// Знайти всі товари з productName + category і вказати, де найдешевше
app.get("/product", (req, res) => {
  const { productName, category } = req.query;
  if (!productName || !category) {
    return res.status(400).json({ error: "Потрібно вказати title і category" });
  }

  const result = findCheapestProductByProductNameAndCategory(
    productName,
    category
  );
  res.json(result);
});

app.listen(PORT, () => {
  console.log(`🚀 Сервер запущено на http://localhost:${PORT}`);
});
