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
      // Витягуємо назву товару
      const title =
        document.querySelector("h1.page-title")?.innerText.trim() || "";

      // Перевірка наявності товару
      const unavailableText =
        document.querySelector(".available-tag--grey")?.innerText || "";
      const isUnavailable = unavailableText.includes("Немає в наявності");

      if (isUnavailable) {
        return { title, unavailable: true };
      }

      // Витягуємо елемент з ціною
      const priceTopEl = document.querySelector(".product-price__top");
      let priceText = "";

      if (priceTopEl) {
        // Витягуємо цілу частину і копійки окремо
        const main =
          priceTopEl.querySelector("span")?.childNodes[0]?.textContent || "";
        const coin =
          priceTopEl.querySelector(".product-price__coin")?.textContent || "";
        priceText = main + coin;
      }
      // Формування ціни
      const price = parseFloat(priceText.replace(/[^\d.]/g, ""));

      // Витягуємо URL зображення товару
      const image =
        document.querySelector(".cardproduct-tabs__item.current picture img")
          ?.src || "";

      // Визначаємо категорію товару з "хлібних крихт"
      const breadcrumbs = document.querySelectorAll(
        ".breadcrumbs__list .breadcrumbs__item a"
      );
      const category =
        breadcrumbs.length >= 3 ? breadcrumbs[2].innerText.trim() : "";

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

  const normalizePrice = (p) =>
    typeof p === "string"
      ? parseFloat(p.replace(/[^\d.,]/g, "").replace(",", "."))
      : parseFloat(p);

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

      const oldPrice = existingData ? normalizePrice(existingData.price) : null;
      const newPrice = normalizePrice(productData.price);

      const hasChanges =
        !existingData ||
        oldPrice !== newPrice ||
        existingData.title !== productData.title ||
        existingData.image !== productData.image ||
        existingData.category !== productData.category;

      if (hasChanges) {
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
      } else {
        console.log(`⏩ Без змін: ${product.productName}`);
      }
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
