package com.moksh.imposterai.dtos;

import com.moksh.imposterai.dtos.enums.MatchState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    private String id;
    private PlayerDto playerOne;
    private PlayerDto playerTwo;
    private MatchState matchState;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ChatResponse> chats;
}
