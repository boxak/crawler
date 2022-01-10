package com.javas.crawler.utils;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrawlingUtils {
    public static String getDateStr(int d) {
        return getDateStr(Calendar.getInstance(),d);
    }

    private static String getDateStr(Calendar calendar, int d) {
        calendar.add(Calendar.DATE, d);
        return new SimpleDateFormat("YYYYMMdd").format(calendar.getTime());
    }

    public static Map<String, Map<String,String>> getPressList() {
        byte[] data;
        //Resource resource = resourceLoader.getResource("classpath:/static/resources/pressList.json");
        Map<String, Map<String,String>> map = new HashMap<>();
        try(InputStream in = CrawlingUtils.class.getResourceAsStream("/static/resources/pressList.json")) {
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

    public static Map<String, Map<String, String>> getClassfication() {
        byte[] data;
        Map<String,Map<String, String>> map = new HashMap<>();
        try(InputStream in = CrawlingUtils.class.getResourceAsStream("/static/resources/newsClassification.json")) {
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
}
