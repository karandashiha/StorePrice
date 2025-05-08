const fs = require("fs");
const path = require("path");

const stores = ["ATB", "Varus", "Silpo"];

const categoryMap = require("./categoryMap");

// Нормалізація категорій для порівняння
function normalizeCategory(rawCategory) {
  const key = rawCategory.toLowerCase().trim();
  return categoryMap[key] || key;
}

function getStoreJsonPath(store) {
  return path.join(
    __dirname,
    "..",
    store,
    "json",
    `scrapedResults${store}.json`
  );
}

function loadAllProducts() {
  const allProducts = [];
  for (const store of stores) {
    const filePath = getStoreJsonPath(store);
    if (fs.existsSync(filePath)) {
      const fileData = fs.readFileSync(filePath, "utf8");
      const json = JSON.parse(fileData);
      allProducts.push(...json.map((p) => ({ ...p, store })));
    } else {
      console.warn(`⚠️ Не знайдено файл: ${filePath}`);
    }
  }
  return allProducts;
}

function getProductsByCategory(rawCategory) {
  const normCategory = normalizeCategory(rawCategory);
  const all = loadAllProducts();
  return all.filter((p) => normalizeCategory(p.category) === normCategory);
}

function findCheapestProductByProductNameAndCategory(
  rawProductName,
  rawCategory
) {
  const normCategory = normalizeCategory(rawCategory);
  const all = loadAllProducts();

  const matches = all.filter(
    (p) =>
      normalizeCategory(p.category) === normCategory &&
      p.productName.toLowerCase().includes(rawProductName.toLowerCase())
  );

  if (matches.length === 0) return { message: "Товар не знайдено" };

  const sorted = matches
    .slice()
    .sort((a, b) => parseFloat(a.price) - parseFloat(b.price));

  return {
    cheapest: {
      productName: sorted[0].productName,
      title: sorted[0].title,
      price: sorted[0].price,
      store: sorted[0].store,
    },
    allMatches: matches,
  };
}

module.exports = {
  getProductsByCategory,
  findCheapestProductByProductNameAndCategory,
  normalizeCategory,
};
