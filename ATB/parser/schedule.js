const cron = require("node-cron");
const fs = require("fs");
const scrapeProduct = require("../scrape-atb"); // той, що ти вже маєш
const productList = require("./productsATB.json"); // твій JSON з товарами

// Планування: кожної середи о 10:00 ранку
cron.schedule("0 10 * * 3", async () => {
  console.log("📆 Час парсити товари — середа, 10:00");

  const results = [];

  for (const product of productList["Молочні продукти та яйця"]) {
    if (!product.url) {
      console.log(`❌ Пропускаємо товар без URL: ${product.productName}`);
      results.push({
        productName: product.productName,
        title: null,
        price: null,
      });
      continue;
    }

    const data = await scrapeProduct(product.url);
    results.push({ productName: product.productName, ...data });
  }

  const timestamp = new Date().toISOString().split("T")[0];
  const filename = `results_${timestamp}.json`;

  fs.writeFileSync(filename, JSON.stringify(results, null, 2), "utf-8");
  console.log(`✅ Збережено результат у файл: ${filename}`);
});

console.log("🕒 Планувальник запущено. Очікуємо на середу...");
