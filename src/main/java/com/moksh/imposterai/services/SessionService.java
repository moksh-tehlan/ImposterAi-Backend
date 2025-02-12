package com.moksh.imposterai.services;

import com.moksh.imposterai.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final ConcurrentHashMap<String, WebSocketSession> sessionConcurrentHashMap;

    public UserEntity getUser(String id) {
        WebSocketSession session = getSession(id);
        return (UserEntity) session.getAttributes().get("User");
    }

    public void saveSession(String id, WebSocketSession session) {
        sessionConcurrentHashMap.put(id, session);
    }

    public WebSocketSession getSession(String id) {
        return (WebSocketSession) sessionConcurrentHashMap.get(id);
    }

    public void removeSession(String id) {
        sessionConcurrentHashMap.remove(id);
    }
}