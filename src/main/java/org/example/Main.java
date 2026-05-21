import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import ru.university.quizbot.QuizBot;
import ru.university.quizbot.config.BotConfig;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
            app.registerBot(BotConfig.BOT_TOKEN, new QuizBot());

            System.out.println("✅ Бот успешно запущен!");
            System.out.println("🤖 Имя бота: @" + BotConfig.BOT_USERNAME);
            System.out.println("📊 Ожидание сообщений...");
            System.out.println("Бот работает. Для остановки программы нажмите Ctrl+C\n");

            // Бесконечное ожидание
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}