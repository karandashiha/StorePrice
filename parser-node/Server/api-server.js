const express = require("express");
const cors = require("cors");
const {
  getProductsByProductName,
  findCheapestProductByProductNameAndCategory,
} = require("./utils");

const app = express();
const PORT = 3000;

app.use(cors());

//Знайти товари з productName , де найдешевше
app.get("/product", (req, res) => {
  const { productName } = req.query;
  if (!productName) {
    return res.status(400).json({ error: "Потрібно вказати productName" });
  }

  const result = getProductsByProductName(productName);
  res.json(result);
});
 

// Знайти всі товари з productName + category і вказати, де найдешевше
app.get("/product_category", (req, res) => {
  const { productName, category } = req.query;
  if (!productName || !category) {
    return res
      .status(400)
      .json({ error: "Потрібно вказати productName  і category" });
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
