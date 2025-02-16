package com.moksh.imposterai.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchFoundData {
    private String matchId;
    private String currentTyperId;
    private UserDto opponent;
}