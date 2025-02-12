package com.moksh.imposterai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "game_result")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResultEntity {
    @Id
    @Column(nullable = false)
    private String matchId;
    private boolean vsBot;
}
