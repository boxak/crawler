package com.javas.crawler;

import com.javas.crawler.dto.News;
import com.javas.crawler.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@DataMongoTest
public class CrawlingTest {

    @Autowired
    NewsRepository newsRepository;

    @Qualifier("webApplicationContext")
    @Autowired
    ResourceLoader resourceLoader;

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

    @Test
    void T3() {
        Resource resource = resourceLoader.getResource("classpath:/static/resources/pressList.json");
        log.info(String.valueOf(resource.exists()));

        try {
            StringBuilder sb = new StringBuilder();
            Path path = Paths.get(resource.getURI());
            List<String> content = Files.readAllLines(path);
            content.forEach(str -> sb.append(str));

            String jsonStr = sb.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONObject jsonArray = (JSONObject) parser.parse(String.valueOf(jsonObject.get("pressList")));
            JSONArray jsonArray1 = (JSONArray) parser.parse(String.valueOf(jsonArray.get("press")));

            Map<String, Map<String,String>> map = new HashMap<>();

            for (Object obj : jsonArray1) {
                Map<String, String> tempMap1 = (Map<String, String>)obj;
                String oid = tempMap1.get("oid");
                Map<String, String> tempMap2 = new HashMap<>();
                tempMap2.put("rootDomain",tempMap1.get("rootDomain"));
                tempMap2.put("mediaName",tempMap1.get("mediaName"));
                map.put(oid,tempMap2);
            }

            log.info(map.toString());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void T4() {
        Resource resource = resourceLoader.getResource("classpath:/static/resources/newsClassification.json");
        log.info(String.valueOf(resource.exists()));
        try {
            StringBuilder sb = new StringBuilder();
            Path path = Paths.get(resource.getURI());
            List<String> content = Files.readAllLines(path);
            content.forEach(str -> sb.append(str));

            String jsonStr = sb.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONObject jsonObject1 = (JSONObject) parser.parse(String.valueOf(jsonObject.get("classificationList")));
            JSONArray jsonArray = (JSONArray) parser.parse(String.valueOf(jsonObject1.get("classification")));
            log.info(jsonArray.toString());

            Map<String,Map<String, Object>> map = new HashMap<>();

            for (Object obj : jsonArray) {
                Map<String, Object> tempMap1 = (Map<String, Object>) obj;
                Map<String, Object> tempMap2 = new HashMap<>();

                tempMap2.put("class1",tempMap1.get("class1"));
                tempMap2.put("class2",tempMap1.get("class2"));
                tempMap2.put("sid1",tempMap1.get("sid1"));
                map.put(String.valueOf(tempMap1.get("sid2")),tempMap2);
            }

            log.info(map.toString());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void T5() {
        Calendar calendar = Calendar.getInstance();
        log.info(new SimpleDateFormat("YYYYMMdd").format(calendar.getTime()));
        calendar.add(Calendar.DATE,-1);
        log.info(new SimpleDateFormat("YYYYMMdd").format(calendar.getTime()));
    }
}
