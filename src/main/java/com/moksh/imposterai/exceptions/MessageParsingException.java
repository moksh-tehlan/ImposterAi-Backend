package com.moksh.imposterai.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MessageParsingException extends RuntimeException {
    public MessageParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageParsingException(String message) {
        super(message);
    }
}