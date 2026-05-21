package ru.university.quizbot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.university.quizbot.config.BotConfig;
import ru.university.quizbot.model.SessionManager;
import ru.university.quizbot.model.UserSession;
import ru.university.quizbot.service.QuizGame;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuizBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final SessionManager sessionManager;
    private final QuizGame quizGame;

    public QuizBot() {
        this.telegramClient = new OkHttpTelegramClient(BotConfig.BOT_TOKEN);
        this.sessionManager = new SessionManager();
        this.quizGame = new QuizGame();

        String logMsg = "🤖 Бот викторина запущен: @" + BotConfig.BOT_USERNAME;
        logToConsoleAndFile(logMsg);
    }

    private void logToConsoleAndFile(String message) {
        System.out.println(message);
        try (PrintWriter writer = new PrintWriter(new FileWriter(BotConfig.LOG_FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + " - " + message);
        } catch (Exception e) {
            System.err.println("❌ Ошибка записи лога: " + e.getMessage());
        }
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();
            String userLogin = update.getMessage().getFrom().getUserName();

            logToConsoleAndFile("📩 @" + userLogin + " (" + userName + "): " + message);

            UserSession session = sessionManager.getSession(chatId, userLogin);
            String response;

            if (message.equals("/start")) {
                response = "Привет, " + userName + "! ✨\n\n" +
                        "Я бот-тренажёр английского (Вариант 2: Переводчик-тренажёр).\n" +
                        "Доступные команды:\n" +
                        "/quiz — начать викторину\n" +
                        "/stop — остановить игру (прогресс сбросится)\n" +
                        "/stats — показать мою статистику";
                logToConsoleAndFile("🟢 Отправлен /start @" + userLogin);

            } else if (message.equals("/quiz")) {
                response = quizGame.startGame(session);
                logToConsoleAndFile("🎮 Начало игры для @" + userLogin);

            } else if (message.equals("/stop")) {
                response = quizGame.stopGame(session);
                logToConsoleAndFile("🛑 Игра остановлена для @" + userLogin);

            } else if (message.equals("/stats")) {
                response = session.getStats();
                logToConsoleAndFile("📊 Запрошена статистика @" + userLogin);

            } else {
                if (!session.isPlaying()) {
                    response = "❌ Игра не активна. Напиши /quiz для начала.";
                    logToConsoleAndFile("⚠️ @" + userLogin + " попытался ответить вне игры");
                } else {
                    String currentWord = session.getCurrentWord();
                    boolean isCorrect = quizGame.checkAnswer(session, message);

                    if (isCorrect) {
                        response = "✅ Правильно! Следующее слово:\n" + session.getCurrentWord();
                        logToConsoleAndFile("✅ @" + userLogin + " правильно перевёл: " + currentWord + " → " + message);
                    } else {
                        String correctTranslation = quizGame.getCurrentTranslation(currentWord);
                        response = "❌ Неправильно. Правильный перевод: " + correctTranslation +
                                "\nПопробуй ещё раз:\n" + session.getCurrentWord();
                        logToConsoleAndFile("❌ @" + userLogin + " ошибся: " + currentWord + " → " + message +
                                " (правильно: " + correctTranslation + ")");
                    }

                    if (!session.hasNextWord()) {
                        response += "\n\n🎉 Поздравляю! Ты прошёл все слова!\n" + session.getStats();
                        logToConsoleAndFile("🏆 @" + userLogin + " завершил игру! Результат: " + session.getStats());
                    }
                }
            }

            sendMessage(chatId, response);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка отправки: " + e.getMessage());
        }
    }
}