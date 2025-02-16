package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;
import lombok.Getter;

@Getter
public abstract class GameException extends RuntimeException {
    private final ErrorCode errorCode;

    protected GameException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected GameException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}