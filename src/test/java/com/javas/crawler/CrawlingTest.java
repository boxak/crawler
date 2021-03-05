package com.javas.crawler;

import com.javas.crawler.dto.News;
import com.javas.crawler.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@DataMongoTest
public class CrawlingTest {

    @Autowired
    NewsRepository newsRepository;

    @Test
    void T1() {
        try {

            String url ="https://news.naver.com/main/read.nhn?mode=LS2D&mid=shm&sid1=102&sid2=276&oid=022&aid=0003558477";

            Document document = Jsoup.connect(url).get();
            String title = document.select("meta[property^=og:title]").get(0).attr("content");
            String summary = document.select(".media_end_summary").get(0).text();
            String content = document.select("._article_body_contents").get(0).text();
            String pubDate = document.select(".t11").get(0).text();
            String mediaName = document.select(".press_logo").select("img").attr("title");
            String rootDomain = document.select(".press_logo").select("a").attr("href");

            content = content.replaceFirst(summary,"");

            log.info("title : {}",title);
            log.info("summary : {}",summary);
            log.info("content : {}",content);
            log.info("pubDate : {}",pubDate);
            log.info("mediaName : {}",mediaName);
            log.info("rootDomain : {}",rootDomain);

            News news = new News();
            news.setTitle(title);
            news.setSummary(summary);
            news.setContent(content);
            news.setMediaName(mediaName);
            news.setUri(url);
            news.setRootDomain(rootDomain);
            news.setPubDate(pubDate);
            news.setRegDate(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
            news.setReadCheck(0);

            newsRepository.insert(news);

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
        log.info("contents : {}",list.get(0).getContent());

    }
}
