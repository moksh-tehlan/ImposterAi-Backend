package com.moksh.imposterai.services;

import com.moksh.imposterai.entities.ChatEntity;
import com.moksh.imposterai.entities.MatchEntity;
import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MatchService matchService;
    private final UserServices userServices;

    public ChatEntity saveChat(String matchId, String senderId, String message) throws Exception {
        MatchEntity match = matchService.getMatchEntity(matchId);
        UserEntity user = userServices.loadUserById(senderId);
        ChatEntity chat = ChatEntity.builder()
                .match(match)
                .sender(user)
                .message(message)
                .build();
        return chatRepository.save(chat);
    }

}
