package com.javas.crawler.repository;

import com.javas.crawler.config.MongoDBConfig;
import com.javas.crawler.dto.NewsList;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Import(MongoDBConfig.class)
public interface NewsListRepository extends MongoRepository<NewsList, String> {
  public NewsList findByUri(String url);
}
