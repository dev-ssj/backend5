package org.example.backendproject.board.searchlog.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "search-log-index")   //검색을 위해 인덱스 새로 생성
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLogDocument {
    //엘라스틱 서치에 저장되는 검색 데이터

    @Id
    private String id;
    private String keyword;
    private String userId;
    private String searchedAt;

}
