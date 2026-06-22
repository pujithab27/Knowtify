package com.knowtify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    private String userAnswer;
    private Boolean isCorrect;
    private Integer timeSpentSeconds;
}
