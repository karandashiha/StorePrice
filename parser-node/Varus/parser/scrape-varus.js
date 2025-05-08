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
    await page.waitForSelector("h1.sf-heading__title", { timeout: 10000 });

    const data = await page.evaluate(() => {
      // Витягуємо назву товару
      const title =
        document.querySelector("h1.sf-heading__title")?.innerText.trim() || "";

      // Перевірка на наявність товару
      const unavailableButton = document.querySelector(".btn-not-available");
      if (
        unavailableButton &&
        unavailableButton.innerText.includes("Товар закінчився")
      ) {
        return { title, unavailable: true }; // Товар недоступний
      }

      // Витягуємо елемент з ціною
      // Отримуємо елемент блоку ціни, обробляючи всі можливі варіанти
      const priceBlock = document.querySelector(
        ".m-product-short-info__price-section .sf-price"
      );
      if (!priceBlock) {
        return { title, price: null };
      }

      const specialText = priceBlock
        .querySelector("ins.sf-price__special")
        ?.innerText.trim();
      const regularText = priceBlock
        .querySelector("span.sf-price__regular")
        ?.innerText.trim();
      const oldText = priceBlock
        .querySelector("del.sf-price__old")
        ?.innerText.trim();

      const parsePrice = (text) =>
        parseFloat(text.replace(/[^\d.,]/g, "").replace(",", "."));

      let price = null;

      if (specialText) {
        price = parsePrice(specialText);
      } else if (regularText) {
        price = parsePrice(regularText);
      } else if (oldText) {
        price = parsePrice(oldText); // якщо все інше відсутнє
      }

      // Витягуємо URL зображення товару
      const image = document.querySelector(".sf-image picture img")?.src || "";

      // Визначаємо категорію товару з "хлібних крихт"
      const breadcrumbs = document.querySelectorAll(
        ".sf-breadcrumbs__breadcrumb"
      );
      const category =
        breadcrumbs.length >= 2
          ? breadcrumbs[breadcrumbs.length - 2].innerText.trim()
          : "";

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
  const jsonFolderPath = path.join(__dirname, "..", "json");

  const productsPath = path.join(jsonFolderPath, "productsVarus.json");
  const scrapedResultsPath = path.join(
    jsonFolderPath,
    "scrapedResultsVarus.json"
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

      const normalizePrice = (p) =>
        typeof p === "string"
          ? parseFloat(p.replace(/[^\d.,]/g, "").replace(",", "."))
          : parseFloat(p);

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
