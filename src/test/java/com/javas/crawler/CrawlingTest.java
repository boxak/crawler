package com.javas.crawler;

import com.javas.crawler.dto.News;
import com.javas.crawler.repository.NewsRepository;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@Slf4j
@DataMongoTest
public class CrawlingTest {

    @Autowired
    NewsRepository newsRepository;

    @Test
    void T1() {
        try {

            String url ="https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=100&oid=003&aid=0010373956";

            Document document = Jsoup.connect(url).get();
            String title = document.select("meta[property^=og:title]").get(0).attr("content");
            String summary = document.select(".media_end_summary").get(0).text();
            String content = document.select("._article_body_contents").get(0).text();

            content = content.replaceFirst(summary,"");

            log.info("title : {}",title);
            log.info("summary : {}",summary);
            log.info("content : {}",content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void T2() {
        List<News> list = newsRepository.findAllByReadCheck(0);

        log.info("uri : {}",list.get(0).getUri());
        log.info("title : {}",list.get(0).getTitle());
        log.info("contents : {}",list.get(0).getContents());

    }
}
