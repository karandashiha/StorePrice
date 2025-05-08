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
    await page.waitForSelector(".product-page__title", { timeout: 10000 });

    const data = await page.evaluate(() => {
      // Витягуємо назву товару
      let title =
        document.querySelector(".product-page__title")?.innerText.trim() || "";

      // Перевірка на наявність товару
      const soldOutElement = document.querySelector(".quantity__soldout");
      const isSoldOut =
        soldOutElement && soldOutElement.innerText.includes("Товар закінчився");

      // Якщо товар закінчився, не витягуємо ціну і не зберігаємо дані
      if (isSoldOut) {
        return { title, isSoldOut };
      }
      const priceText =
        document.querySelector(".product-page-price__main")?.innerText || "";

      //Нормалізація цін Сільпо: перерахунок з 100г на 1кг у парсері
      let price = priceText.replace(/[^\d.,]/g, "").replace(",", ".");
      price = parseFloat(price);

      if (/100\s?г/.test(title.toLowerCase())) {
        price = parseFloat((price * 10).toFixed(2)); // перерахунок на 1 кг
        title = title.replace(/,\s?100\s?г/i, "").trim();
      }

      // Витягуємо URL зображення товару
      let image = document.querySelector(".product-img")?.src;
      if (!image) {
        const pictureImg = document.querySelector(".product-img picture img");
        image = pictureImg?.getAttribute("src") || "";
      }

      // Визначаємо категорію товару з "хлібних крихт"
      const breadcrumbs = document.querySelectorAll(
        ".breadcrumbs-list__item a"
      );
      const category =
        breadcrumbs.length >= 3
          ? breadcrumbs[breadcrumbs.length - 3].innerText.trim()
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
  const jsonFolderPath =
    "C:/Users/Kathryn/Desktop/Порівняння цін_ЧатБОТ/ParserProducts/StorePrice/Silpo/json";

  const productsPath = path.join(jsonFolderPath, "productsSilpo.json");
  const scrapedResultsPath = path.join(
    jsonFolderPath,
    "scrapedResultsSilpo.json"
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
