package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class ChatException extends GameException {
    public ChatException(String message) {
        super(ErrorCode.CHAT_ERROR, message);
    }

    public ChatException(String message, Throwable cause) {
        super(ErrorCode.CHAT_ERROR, message);
    }
}