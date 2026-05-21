package ru.university.quizbot.service;

import ru.university.quizbot.model.Dictionary;
import ru.university.quizbot.model.UserSession;

public class QuizGame {
    private final Dictionary dictionary;

    public QuizGame() {
        this.dictionary = new Dictionary();
    }

    public String startGame(UserSession session) {
        session.startGame(dictionary);
        return "🎓 Игра началась!\nПереведи слово:\n" + session.getCurrentWord();
    }

    public String stopGame(UserSession session) {
        session.stopGame();
        return "🛑 Игра остановлена. Прогресс сброшен. Для новой игры напиши /quiz";
    }

    public boolean checkAnswer(UserSession session, String userAnswer) {
        String currentWord = session.getCurrentWord();
        boolean isCorrect = dictionary.checkTranslation(currentWord, userAnswer);
        session.recordAnswer(isCorrect, currentWord, dictionary);
        return isCorrect;
    }

    public String getCurrentTranslation(String word) {
        return dictionary.getTranslation(word);
    }
}