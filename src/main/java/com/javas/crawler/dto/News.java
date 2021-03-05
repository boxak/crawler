package com.javas.crawler.dto;

import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "crawling")
public class News {
  @Id
  @Nullable
  String _id;

  @Nullable
  String title;

  @Nullable
  String content;

  @Nullable
  String summary;

  @Nullable
  String mediaName;

  @Nullable
  String uri;

  @Nullable
  String category;

  @Nullable
  String rootDomain;

  @Nullable
  String pubDate;

  @Nullable
  String regDate;

  int readCheck;
}
