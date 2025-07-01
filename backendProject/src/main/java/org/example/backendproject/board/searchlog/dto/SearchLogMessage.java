package org.example.backendproject.board.searchlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogMessage {

    //kafka로 주고받는 메시지 포멧(DTO)
    private String keyword;     //검색된 키워드
    private String userId;      //검색한 유저 Id
    private String searchedAt;  //검색한 시간
    
    
}
