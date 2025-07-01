package org.example.backendproject.board.searchlog.service;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.board.searchlog.domain.SearchLogDocument;
import org.example.backendproject.board.searchlog.repository.SearchLogRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchLogEsService {
    
    //엘라스틱 서치 저장, 통계 집계 비즈니스 로직 서비스
    
    private final SearchLogRepository searchLogRepository;
    
    //카프카에서 전달 받은 검색 데이터 저장 메서드
    public void save(SearchLogDocument searchLogDocument){
        searchLogRepository.save(searchLogDocument);
    }
}
