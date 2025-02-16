package com.moksh.imposterai.exceptions;

import com.moksh.imposterai.dtos.enums.ErrorCode;

public class PlayerNotFoundException extends GameException {
  public PlayerNotFoundException(String playerId) {
    super(ErrorCode.PLAYER_NOT_FOUND, "Player not found: " + playerId);
  }
}
