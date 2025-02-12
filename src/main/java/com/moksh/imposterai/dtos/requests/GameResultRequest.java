package com.moksh.imposterai.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResultRequest {
    boolean isOpponentAHuman;
    String matchId;
}
