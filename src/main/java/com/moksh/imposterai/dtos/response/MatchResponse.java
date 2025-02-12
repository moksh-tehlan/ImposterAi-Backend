package com.moksh.imposterai.dtos.response;

import com.moksh.imposterai.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {
    private String matchId;
    private String currentTyperId;
    private UserDto opponent;
}
