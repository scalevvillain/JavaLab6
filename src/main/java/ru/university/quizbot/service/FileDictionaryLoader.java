package ru.university.quizbot.service;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class FileDictionaryLoader {

    public static Map<String, String> loadFromFile(String resourcePath) {
        Map<String, String> dictionary = new LinkedHashMap<>();  // ← LinkedHashMap сохраняет порядок

        try (InputStream inputStream = FileDictionaryLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("❌ Файл не найден: " + resourcePath);
                return getDefaultDictionary();
            }

            Scanner scanner = new Scanner(inputStream, "UTF-8");
            int lineNumber = 0;

            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String english = parts[0].trim().toLowerCase();
                    String russian = parts[1].trim();
                    dictionary.put(english, russian);
                    System.out.println("📖 Загружено слово: " + english + " → " + russian);
                } else {
                    System.err.println("⚠️ Ошибка в строке " + lineNumber + ": " + line);
                }
            }

            System.out.println("✅ Загружено " + dictionary.size() + " слов из файла");

        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки файла: " + e.getMessage());
            return getDefaultDictionary();
        }

        if (dictionary.isEmpty()) {
            System.out.println("⚠️ Файл пуст, использую словарь по умолчанию");
            return getDefaultDictionary();
        }

        return dictionary;
    }

    private static Map<String, String> getDefaultDictionary() {
        Map<String, String> defaultDict = new LinkedHashMap<>();
        defaultDict.put("apple", "яблоко");
        defaultDict.put("dog", "собака");
        defaultDict.put("cat", "кошка");
        defaultDict.put("sun", "солнце");
        defaultDict.put("moon", "луна");
        defaultDict.put("car", "машина");
        defaultDict.put("house", "дом");
        defaultDict.put("happy", "счастливый");
        defaultDict.put("big", "большой");
        defaultDict.put("blue", "синий");
        return defaultDict;
    }
}
