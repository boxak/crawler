package com.javas.crawler.batch;

import com.javas.crawler.dto.News;
import com.javas.crawler.repository.NewsRepository;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class CrawlingScheduler {

    @Qualifier("webApplicationContext")
    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    CacheManager cacheManager;

    @Scheduled(cron = "0/3 5 4 * * *")
    @Async("asyncThreadTaskExecutor")
    public void crawling_naver_main_news() throws IOException, ParseException {
        String news_list_url_format = "https://news.naver.com/main/list.nhn?" +
            "mode=LS2D" +
            "&mid=shm" +
            "&sid1=%s" +
            "&sid2=%s"+
            "&date=%s"+
            "&page=%d";
        Resource resource = resourceLoader.getResource("classpath:/static/resources/newsClassification.json");
        log.info(String.valueOf(resource.exists()));

        Map<String, Map<String, String>> typeMap = getClassfication();
        for (String sid2 : typeMap.keySet()) {
            String sid1 = typeMap.get(sid2).get("sid1");
            String class1 = typeMap.get(sid2).get("class1");
            String class2 = typeMap.get(sid2).get("class2");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,-1);
            String date = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
            int page = 1;
            roop1 : while (true) {
                while (true) {
                    String news_list_url = String.format(news_list_url_format,
                        sid1, sid2, date, page);
                    log.info(news_list_url);
                    Cache cache = cacheManager.getCache("newsListCache");
                    Document document = null;
                    if (ObjectUtils.isEmpty(cache.get(news_list_url))) {
                        document = Jsoup.connect(news_list_url).get();
                        String[] arr = new String[]{".type06_headline", ".type06"};
                        for (String str : arr) {
                            Elements newsList = document.select(str);
                            if (!ObjectUtils.isEmpty(newsList)) {
                                newsList = newsList.select("li");
                            }
                            for (Element elem : newsList) {
                                Elements aElems = elem.select("a");
                                if (!ObjectUtils.isEmpty(aElems)) {
                                    String href = aElems.get(0).attr("href");
                                    log.info(href);
                                    getNewsInfo(href, sid1, sid2, class1, class2);
                                }
                            }
                        }
                    } else {
                        document = (Document) cache.get(news_list_url).get();
                        log.info("news list doc from cache : {}", document.toString());
                    }
                    Elements pageElems = document.getElementsByClass("nclicks(fls.page)");
                    boolean hasNextPage = false;
                    if (!ObjectUtils.isEmpty(pageElems)) {
                        String lastPageStr = pageElems.last().text();
                        if (!"다음".equals(lastPageStr) && !"이전".equals(lastPageStr)) {
                            int lastPage = Integer.parseInt(pageElems.last().text());
                            if (lastPage > page) {
                                hasNextPage = true;
                            }
                        } else if ("다음".equals(lastPageStr)) {
                            hasNextPage = true;
                        }
                    }
                    if (!hasNextPage) {
                        break roop1;
                    }
                    page++;
                }
            }
        }
    }

    private void getNewsInfo(String url,String sid1,String sid2, String class1, String class2) throws IOException, ParseException {
        if (hasNewsDoc(url)) return;
        if (newsRepository.existsByUri(url)) return;
        Pattern pattern1 = Pattern.compile("oid=[0-9]{3}");
        Matcher matcher = pattern1.matcher(url);
        String oid = "";
        if (matcher.find()) {
            String str = matcher.group();
            oid = str.substring(4);
        }

        Map<String, Map<String, String>> pressMap = getPressList();
        String mediaName = "";
        String rootDomain = "";
        if (pressMap.containsKey(oid)) {
            mediaName = pressMap.get(oid).get("mediaName");
            rootDomain = pressMap.get(oid).get("rootDomain");
        }

        Document document = Jsoup.connect(url).get();

        String title = "";
        String summary = "";
        String content = "";
        String pubDate = "";

        Elements titleElems = document.select("meta[property^=og:title]");
        Elements summaryElems = document.select(".media_end_summary");
        Elements contentElems = document.select("._article_body_contents");
        Elements dateElems = document.select(".t11");

        if (!ObjectUtils.isEmpty(titleElems)) {
            title = titleElems.get(0).attr("content");
        }
        if (!ObjectUtils.isEmpty(summaryElems)) {
            summary = summaryElems.get(0).text();
        }
        if (!ObjectUtils.isEmpty(contentElems)) {
            content = contentElems.get(0).text();
        }
        if (!ObjectUtils.isEmpty(dateElems)) {
            pubDate = dateElems.get(0).text();
        }

        News news = new News();
        news.setTitle(title);
        news.setSummary(summary);
        news.setContent(content);
        news.setMediaName(mediaName);
        news.setUri(url);
        news.setRootDomain(rootDomain);
        news.setPubDate(pubDate);
        news.setSid1(sid1);
        news.setSid2(sid2);
        news.setClass1(class1);
        news.setClass2(class2);
        news.setRegDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        news.setReadCheck(0);

        newsRepository.save(news);

    }

    private Map<String, Map<String,String>> getPressList() {
        byte[] data;
        //Resource resource = resourceLoader.getResource("classpath:/static/resources/pressList.json");
        Map<String, Map<String,String>> map = new HashMap<>();
        try(InputStream in = getClass().getResourceAsStream("/static/resources/pressList.json")) {
//            StringBuilder sb = new StringBuilder();
//            Path path = Paths.get(resource.getURI());
//            List<String> content = Files.readAllLines(path);
//            content.forEach(str -> sb.append(str));

            data = IOUtils.toByteArray(in);

            String jsonStr = new String(data);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONObject jsonArray = (JSONObject) parser.parse(String.valueOf(jsonObject.get("pressList")));
            JSONArray jsonArray1 = (JSONArray) parser.parse(String.valueOf(jsonArray.get("press")));

            for (Object obj : jsonArray1) {
                Map<String, String> tempMap1 = (Map<String, String>)obj;
                String oid = tempMap1.get("oid");
                Map<String, String> tempMap2 = new HashMap<>();
                tempMap2.put("rootDomain",tempMap1.get("rootDomain"));
                tempMap2.put("mediaName",tempMap1.get("mediaName"));
                map.put(oid,tempMap2);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<String, Map<String, String>> getClassfication() {
        byte[] data;
        Map<String,Map<String, String>> map = new HashMap<>();
        try(InputStream in = getClass().getResourceAsStream("/static/resources/newsClassification.json")) {
//            StringBuilder sb = new StringBuilder();
//            Path path = Paths.get(resource.getURI());
//            List<String> content = Files.readAllLines(path);
//            content.forEach(str -> sb.append(str));
            data = IOUtils.toByteArray(in);

            String jsonStr = new String(data);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONObject jsonObject1 = (JSONObject) parser.parse(String.valueOf(jsonObject.get("classificationList")));
            JSONArray jsonArray = (JSONArray) parser.parse(String.valueOf(jsonObject1.get("classification")));

            for (Object obj : jsonArray) {
                Map<String, String> tempMap1 = (Map<String, String>) obj;
                Map<String, String> tempMap2 = new HashMap<>();

                tempMap2.put("class1",tempMap1.get("class1"));
                tempMap2.put("class2",tempMap1.get("class2"));
                tempMap2.put("sid1",tempMap1.get("sid1"));
                map.put(tempMap1.get("sid2"),tempMap2);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return map;
    }

    private boolean hasNewsDoc(String url) {
        Cache cache = cacheManager.getCache("newsCache");
        if (ObjectUtils.isEmpty(cache.get(url))) {
            cache.put(url,url+"_value");
            return false;
        } else return true;
    }
}
