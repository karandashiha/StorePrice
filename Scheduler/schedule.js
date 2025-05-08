const cron = require("node-cron");
const path = require("path");

// Підключаємо парсери відносно від Scheduler
const scrapeATB = require(path.join(
  __dirname,
  "..",
  "ATB",
  "parser",
  "scrape-atb"
)); // Шлях до парсера АТБ
const scrapeVarus = require(path.join(
  __dirname,
  "..",
  "Varus",
  "parser",
  "scrape-varus"
)); // Шлях до парсера Varus
const scrapeSilpo = require(path.join(
  __dirname,
  "..",
  "Silpo",
  "parser",
  "scrape-silpo"
)); // Шлях до парсера Silpo

async function runAllParsers() {
  console.log("🚀 Починаємо парсинг всіх магазинів...");

  try {
    console.log("🛒 Парсимо ATB...");
    await scrapeATB();

    console.log("🛒 Парсимо Varus...");
    await scrapeVarus();

    console.log("🛒 Парсимо Silpo...");
    await scrapeSilpo();

    console.log("✅ Усі парсери успішно виконано!");
  } catch (err) {
    console.error("❌ Помилка при виконанні парсерів:", err);
  }
}

// Розклад: кожен четвер о 10:00
cron.schedule("0 10 * * 4", () => {
  console.log("🕙 Час запустити плановий парсинг!");
  runAllParsers();
});

// Додатково: дозволяємо ручний запуск
if (require.main === module) {
  runAllParsers()
    .then(() => {
      console.log("🛑 Парсинг завершено.");
      process.exit(0);
    })
    .catch((err) => {
      console.error("❌ Сталася помилка при парсингу:", err);
      process.exit(1);
    });
}
