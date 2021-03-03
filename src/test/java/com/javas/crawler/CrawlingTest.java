package com.javas.crawler;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class CrawlingTest {
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
}
