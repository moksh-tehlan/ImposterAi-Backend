package com.moksh.imposterai.dtos.response;

import com.moksh.imposterai.dtos.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    UserDto userDto;
    String token;
}
