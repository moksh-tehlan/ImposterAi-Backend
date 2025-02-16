package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class PlayerOfflineException extends GameException {
    public PlayerOfflineException(String playerId) {
        super(ErrorCode.PLAYER_OFFLINE,
                String.format("Player is offline: %s", playerId));
    }
}