const puppeteer = require("puppeteer-extra");
const StealthPlugin = require("puppeteer-extra-plugin-stealth");
const fs = require("fs");

puppeteer.use(StealthPlugin());

async function scrapeProduct(url) {
  const browser = await puppeteer.launch({ headless: "new" });
  const page = await browser.newPage();

  try {
    console.log("🚀 Відкриваємо:", url);
    await page.goto(url, { waitUntil: "networkidle2", timeout: 60000 });

    // Чекаємо заголовок
    await page.waitForSelector("h1.page-title.product-page__title", {
      timeout: 10000,
    }); // Можливо збільшимо час очікування

    const data = await page.evaluate(() => {
      const title = document.querySelector("h1.page-title")?.innerText.trim();
      const price = document
        .querySelector(".product-price__top")
        ?.innerText.replace(/[^\d.,]/g, "");
      const image = document.querySelector(
        ".cardproduct-tabs__item.current picture img"
      )?.src;

      const breadcrumbs = document.querySelectorAll(
        ".breadcrumbs__list .breadcrumbs__item a"
      );
      const category = breadcrumbs[breadcrumbs.length - 1]?.innerText.trim(); // <- конкретна підкатегорія

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
  const products = JSON.parse(
    fs.readFileSync(
      "C:/Users/Kathryn/Desktop/Порівняння цін_ЧатБОТ/ParserProducts/StorePrice/ATB/json/productsATB.json",
      "utf8"
    )
  );
  const results = [];

  // Читання старих результатів, якщо файл існує
  let scrapedResults = [];
  if (fs.existsSync("scrapedResultsATB.json")) {
    scrapedResults = JSON.parse(
      fs.readFileSync("scrapedResultsATB.json", "utf8")
    );
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

  console.log("Результати:", JSON.stringify(results, null, 2));
  fs.writeFileSync("scrapedResultsATB.json", JSON.stringify(results, null, 2));
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
