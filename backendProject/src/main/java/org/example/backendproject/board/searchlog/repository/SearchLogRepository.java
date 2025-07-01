package org.example.backendproject.board.searchlog.repository;

import org.apache.kafka.common.protocol.types.Field;
import org.example.backendproject.board.searchlog.domain.SearchLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchLogRepository extends ElasticsearchRepository<SearchLogDocument, String> {

    //엘라스틱 서치 저장/검색용 레포지토리

}
