package com.javas.crawler.repository;

import com.javas.crawler.config.MongoDBConfig;
import com.javas.crawler.dto.News;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Import(MongoDBConfig.class)
public interface NewsRepository extends MongoRepository<News, String> {
  List<News> findAllByReadCheck(int read);
  boolean existsByUri(String url);
}
