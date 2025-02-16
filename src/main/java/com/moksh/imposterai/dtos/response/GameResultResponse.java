package com.moksh.imposterai.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameResultResponse {
    @JsonProperty("isCorrectAnswer")
    boolean isCorrectAnswer;
}
