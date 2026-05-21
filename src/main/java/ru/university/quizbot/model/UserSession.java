package ru.university.quizbot.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;

public class UserSession {
    private final long userId;
    private final String userName;
    private boolean isPlaying = false;
    private String currentWord;
    private final Queue<String> wordQueue = new LinkedList<>();
    private final Set<String> learnedWords = new HashSet<>();
    private int correctAnswers = 0;
    private int wrongAnswers = 0;

    public UserSession(long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public void startGame(Dictionary dictionary) {
        isPlaying = true;
        correctAnswers = 0;
        wrongAnswers = 0;
        wordQueue.clear();

        // Берём все слова из словаря
        for (String word : dictionary.getAllWords().keySet()) {
            wordQueue.add(word);
        }

        currentWord = wordQueue.poll();
    }

    public void stopGame() {
        isPlaying = false;
        currentWord = null;
        wordQueue.clear();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void recordAnswer(boolean isCorrect, String word, Dictionary dictionary) {
        if (isCorrect) {
            correctAnswers++;
            learnedWords.add(word);
        } else {
            wrongAnswers++;
            // При ошибке слово возвращается в конец очереди
            if (currentWord != null) {
                wordQueue.add(currentWord);
            }
        }
        // Следующее слово
        currentWord = wordQueue.poll();
        if (currentWord == null) {
            isPlaying = false;
        }
    }

    public String getStats() {
        return "📊 Статистика @" + userName + ":\n" +
                "✅ Правильных ответов: " + correctAnswers + "\n" +
                "❌ Неправильных: " + wrongAnswers + "\n" +
                "📚 Выучено слов: " + learnedWords.size() + "/" + (correctAnswers + wrongAnswers);
    }

    public boolean hasNextWord() {
        return currentWord != null;
    }

    public long getUserId() {
        return userId;
    }
}