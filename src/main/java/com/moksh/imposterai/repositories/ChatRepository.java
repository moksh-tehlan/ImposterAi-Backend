package com.moksh.imposterai.repositories;

import com.moksh.imposterai.entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, String> {
    List<ChatEntity> findByMatchIdOrderBySendAtAsc(String matchId);
}
