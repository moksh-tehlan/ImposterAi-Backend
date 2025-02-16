package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class PlayerAlreadyInMatchException extends GameException {
    public PlayerAlreadyInMatchException(String playerId) {
        super(ErrorCode.PLAYER_ALREADY_IN_MATCH, "Player already in match: " + playerId);
    }
}