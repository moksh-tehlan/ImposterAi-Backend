package com.moksh.imposterai.entities;

import com.moksh.imposterai.dtos.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "players")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerEntity {
    @Id
    private String sessionId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    @Builder.Default
    private Boolean isBot = false;
}
