package ru.university.quizbot.model;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private final Map<Long, UserSession> sessions = new HashMap<>();

    public UserSession getSession(long userId, String userName) {
        return sessions.computeIfAbsent(userId, id -> new UserSession(id, userName));
    }

    public void removeSession(long userId) {
        sessions.remove(userId);
    }
}