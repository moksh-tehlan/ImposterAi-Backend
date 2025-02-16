package com.moksh.imposterai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moksh.imposterai.dtos.ChatMessage;
import com.moksh.imposterai.dtos.response.WsMessage;
import com.moksh.imposterai.entities.ChatEntity;
import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.exceptions.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerChattingService {
    private final MatchService matchService;
    private final PlayerService playerService;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final NotificationService chatNotificationService;

    public void handleChat(WebSocketSession session, WsMessage<?> wsMessage) {
        String sessionId = session.getId();
        log.debug("Handling chat message from session: {}", sessionId);

        // Parse chat message
        ChatMessage chatMessage = parseChatMessage(wsMessage);

        // Get player and match
        PlayerEntity player = getPlayer(sessionId);
        MatchEntity match = getMatch(player);

        // Save chat and notify
        ChatEntity savedChat = saveAndNotify(match, player, chatMessage);
        log.debug("Successfully handled chat message: {}", savedChat.getId());


    }

    private ChatMessage parseChatMessage(WsMessage<?> wsMessage) {
        try {
            return objectMapper.convertValue(wsMessage.getData(), ChatMessage.class);
        } catch (Exception e) {
            throw new ChatException("Invalid chat message format", e);
        }
    }

    private PlayerEntity getPlayer(String sessionId) {
        return playerService.getPlayerBySessionId(sessionId);
    }

    private MatchEntity getMatch(PlayerEntity player) {
        return matchService.findByPlayerId(player.getSessionId());
    }

    private ChatEntity saveAndNotify(MatchEntity match, PlayerEntity player, ChatMessage chatMessage) {
        ChatEntity savedChat = chatService.saveChat(
                match.getId(),
                player.getUser().getId(),
                chatMessage.getMessage()
        );

        chatNotificationService.sendChatMessage(match, player.getSessionId(), savedChat);
        return savedChat;
    }
}
