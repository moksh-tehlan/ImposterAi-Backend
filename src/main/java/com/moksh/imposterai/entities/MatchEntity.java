package com.moksh.imposterai.entities;

import com.moksh.imposterai.dtos.enums.MatchState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "player_one_id", unique = true)
    private PlayerEntity playerOne;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "player_two_id", unique = true)
    private PlayerEntity playerTwo;

    private String currentTyperId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchState matchState;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatEntity> chats = new ArrayList<>();

    public Optional<PlayerEntity> getOpponent(String sessionId) {
        if (playerOne.getSessionId().equals(sessionId)) return Optional.of(playerTwo);
        if (playerTwo.getSessionId().equals(sessionId)) return Optional.of(playerOne);
        return Optional.empty();
    }
}
