package ru.university.quizbot.model;

import ru.university.quizbot.service.FileDictionaryLoader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    private final Map<String, String> words;

    public Dictionary() {
        // Загружаем слова в LinkedHashMap (сохраняет порядок)
        this.words = new LinkedHashMap<>();
        Map<String, String> loaded = FileDictionaryLoader.loadFromFile("/words.txt");
        this.words.putAll(loaded);
    }

    public boolean checkTranslation(String word, String userAnswer) {
        String correct = words.get(word.toLowerCase());
        if (correct == null) return false;
        return correct.equalsIgnoreCase(userAnswer.trim().toLowerCase());
    }

    public String getRandomWord() {
        List<String> keys = new ArrayList<>(words.keySet());
        return keys.get((int) (Math.random() * keys.size()));
    }

    public String getTranslation(String word) {
        return words.get(word.toLowerCase());
    }

    public Map<String, String> getAllWords() {
        return words;
    }

    public int size() {
        return words.size();
    }
}