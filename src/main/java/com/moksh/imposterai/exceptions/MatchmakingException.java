package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class MatchmakingException extends GameException {
    public MatchmakingException(String message, Throwable cause) {
        super(ErrorCode.MATCHMAKING_ERROR, message, cause);
    }
}
