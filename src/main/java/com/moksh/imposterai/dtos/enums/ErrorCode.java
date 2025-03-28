package com.moksh.imposterai.dtos.enums;

import lombok.Getter;

public enum ErrorCode {
    MATCHMAKING_ERROR(1000, "Error during matchmaking"),
    PLAYER_NOT_FOUND(1001, "Player not found"),
    PLAYER_ALREADY_IN_MATCH(1002, "Player is already in a match"),
    MATCH_NOT_FOUND(1003, "Match not found"),
    PLAYER_OFFLINE(1004, "Player is offline"),
    WEBSOCKET_ERROR(1005, "WebSocket communication error"),
    INVALID_MESSAGE(1006, "Invalid message format"),
    CHAT_ERROR(1007, "Chat operation failed"),
    ACCOUNT_NOT_VERIFIED(4003, "Account is not verified");

    @Getter
    private final int code;
    @Getter
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

}