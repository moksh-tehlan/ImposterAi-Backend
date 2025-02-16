package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class MatchNotFoundException extends GameException {
    public MatchNotFoundException(String matchId) {
        super(ErrorCode.MATCH_NOT_FOUND,
                String.format("Match not found with ID: %s", matchId));
    }
}
