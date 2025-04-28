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

    // Чекаємо, поки завантажиться товар
    await page.waitForSelector("h1.sf-heading__title", { timeout: 10000 }); // Можливо збільшимо час очікування

    const data = await page.evaluate(() => {
      const title = document
        .querySelector("h1.sf-heading__title")
        ?.innerText.trim();
      const priceText =
        document.querySelector(".sf-price__regular")?.innerText || "";
      const price = priceText.replace(/[^\d.,]/g, "").replace(",", ".");
      const image = document.querySelector(".sf-image picture img")?.src;

      const categories = document.querySelectorAll(
        ".sf-breadcrumbs__breadcrumb"
      );
      const category = categories[categories.length - 2]?.innerText.trim();

      return {
        title,
        price,
        image,
        category,
      };
    });

    if (!data.title || !data.price) {
      console.log("❌ Не вдалося отримати дані для цього товару:", url);
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
    "C:/Users/Kathryn/Desktop/Порівняння цін_ЧатБОТ/ParserProducts/StorePrice/Varus/json";

  // Читання старих результатів, якщо файл існує
  const productsPath = path.join(jsonFolderPath, "productsVarus.json");
  const scrapedResultsPath = path.join(
    jsonFolderPath,
    "scrapedResultsVarus.json"
  );

  const products = JSON.parse(fs.readFileSync(productsPath, "utf8"));

  const results = [];
  let scrapedResults = [];

  if (fs.existsSync(scrapedResultsPath)) {
    scrapedResults = JSON.parse(fs.readFileSync(scrapedResultsPath, "utf8"));
  }

  // Створюємо мапу для оновлення результатів
  const resultsMap = new Map();
  scrapedResults.forEach((item) => resultsMap.set(item.productName, item));

  for (const category in products) {
    const categoryProducts = products[category];

    for (const product of categoryProducts) {
      if (product.url) {
        const productData = await scrapeProduct(product.url);
        if (productData) {
          // Перевірка чи товар вже є в результатах
          if (resultsMap.has(product.productName)) {
            const existingData = resultsMap.get(product.productName);

            // Якщо ціна або інші дані змінилися, оновлюємо
            if (
              existingData.price !== productData.price ||
              existingData.category !== productData.category
            ) {
              console.log(`💡 Оновлення товару: ${product.productName}`);
              resultsMap.set(product.productName, {
                productName: product.productName,
                ...productData,
              });
            }
          } else {
            // Якщо товар новий, додаємо його в результати
            console.log(`➕ Додавання нового товару: ${product.productName}`);
            resultsMap.set(product.productName, {
              productName: product.productName,
              ...productData,
            });
          }
        }
      } else {
        console.log(`❌ Пропускаємо товар без URL: ${product.productName}`);
      }
    }
  }

  // Перетворюємо мапу назад в масив для запису в файл
  results.push(...Array.from(resultsMap.values()));

  console.log("Результати парсингу:", JSON.stringify(results, null, 2));
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

module.exports = scrapeProduct;
