package com.javas.crawler;

import com.javas.crawler.repository.NewsRepository;
import com.javas.crawler.utils.CrawlingUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.io.IOException;
import java.util.Map;

@Slf4j
@DataMongoTest
public class PopularNewsCrawlingTest {

    @Autowired
    NewsRepository newsRepository;

    @Test
    void T1() {
        String ranking_press_uri = "https://media.naver.com/press/%s/ranking";

        Map<String, Map<String, String>> pressMap = CrawlingUtils.getPressList();

        for (String oid : pressMap.keySet()) {
            ranking_press_uri = String.format(ranking_press_uri, oid);
            try {
                Document document = Jsoup.connect(ranking_press_uri).get();
                Elements newsElements = document.getElementsByClass("_es_pc_link");
                for (Element elem : newsElements) {
                    System.out.println(elem.attr("href"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
