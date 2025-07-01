package org.example.backendproject.board.elasticsearch.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

//퍼센트, 순위포함 DTO
@Data
@AllArgsConstructor
public class TopKeywordDTO {
    private String keyword;
    private long count;
    private double percentage;
}
