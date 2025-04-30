const puppeteer = require("puppeteer-extra");
const StealthPlugin = require("puppeteer-extra-plugin-stealth");
const fs = require("fs");
const path = require("path");

puppeteer.use(StealthPlugin());

async function scrapeProduct(url) {
  const browser = await puppeteer.launch({ headless: true });
  const page = await browser.newPage();

  try {
    console.log("🚀 Відкриваємо:", url);
    await page.goto(url, { waitUntil: "networkidle2", timeout: 60000 });

    // Очікуємо заголовок
    await page.waitForSelector("h1.page-title", { timeout: 10000 });

    const data = await page.evaluate(() => {
      const title =
        document.querySelector("h1.page-title")?.innerText.trim() || "";
      const priceText =
        document.querySelector(".product-price__top")?.innerText || "";
      const price = priceText.replace(/[^\d.,]/g, "").replace(",", ".");
      const image =
        document.querySelector(".cardproduct-tabs__item.current picture img")
          ?.src || "";

      const breadcrumbs = document.querySelectorAll(
        ".breadcrumbs__list .breadcrumbs__item a"
      );
      const category =
        breadcrumbs[breadcrumbs.length - 2]?.innerText.trim() || "";

      return { title, price, image, category };
    });

    if (!data.title || !data.price) {
      console.log("❌ Не вдалося отримати дані для цього товару:", url);
      await browser.close();
      return null;
    }

    console.log("✅ Отримано:", data);
    await browser.close();
    return data;
  } catch (err) {
    console.error("❌ Помилка при парсингу:", err.message);
    await browser.close();
    return null;
  }
}

async function scrapeProductsFromJson() {
  const jsonFolderPath =
    "C:/Users/Kathryn/Desktop/Порівняння цін_ЧатБОТ/ParserProducts/StorePrice/ATB/json";

  const productsPath = path.join(jsonFolderPath, "productsATB.json");
  const scrapedResultsPath = path.join(
    jsonFolderPath,
    "scrapedResultsATB.json"
  );

  const products = JSON.parse(fs.readFileSync(productsPath, "utf8"));
  let scrapedResults = [];

  if (fs.existsSync(scrapedResultsPath)) {
    scrapedResults = JSON.parse(fs.readFileSync(scrapedResultsPath, "utf8"));
  }

  const resultsMap = new Map();
  scrapedResults.forEach((item) => resultsMap.set(item.url, item));

  for (const category in products) {
    const categoryProducts = products[category];

    for (const product of categoryProducts) {
      if (!product.url) {
        console.log(`❌ Пропущено товар без URL: ${product.productName}`);
        continue;
      }

      const productData = await scrapeProduct(product.url);
      if (!productData) {
        console.log(`⚠️ Пропущено (помилка): ${product.url}`);
        continue;
      }

      const key = product.url;
      const existingData = resultsMap.get(key);

      if (
        existingData &&
        existingData.price === productData.price &&
        existingData.category === productData.category
      ) {
        continue; // Дані не змінились
      }

      if (existingData) {
        console.log(`♻️ Оновлення товару: ${product.productName}`);
      } else {
        console.log(`➕ Додавання нового товару: ${product.productName}`);
      }

      resultsMap.set(key, {
        productName: product.productName,
        url: product.url,
        ...productData,
      });
    }
  }

  const results = Array.from(resultsMap.values());
  console.log("\n✅ Збережено товарів:", results.length);
  fs.writeFileSync(
    scrapedResultsPath,
    JSON.stringify(results, null, 2),
    "utf8"
  );
}

if (require.main === module) {
  scrapeProductsFromJson()
    .then(() => {
      console.log("✅ Парсинг завершено!");
    })
    .catch((err) => {
      console.error("❌ Сталася помилка:", err);
    });
}

module.exports = scrapeProductsFromJson;
