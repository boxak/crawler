package com.javas.crawler.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@PropertySource("static/resources/batch.properties")
public class CrawlingScheduler {
    @Scheduled(fixedDelay = 2000)
    public void crawling_naver_main_news() {

    }
}
