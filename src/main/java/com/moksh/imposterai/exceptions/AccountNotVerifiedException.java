package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class AccountNotVerifiedException extends GameException {
    public AccountNotVerifiedException(String message) {
        super(ErrorCode.ACCOUNT_NOT_VERIFIED, message);
    }
}
