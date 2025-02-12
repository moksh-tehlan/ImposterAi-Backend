package com.moksh.imposterai.dtos;

import com.moksh.imposterai.dtos.enums.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {
    private String sessionId;
    private UserDto user;
    private MatchStatus matchStatus;
}
