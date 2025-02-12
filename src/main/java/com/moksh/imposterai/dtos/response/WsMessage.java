package com.moksh.imposterai.dtos.response;

import com.moksh.imposterai.dtos.enums.SocketActions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsMessage<T> {
    private SocketActions action;
    private T data;
}
