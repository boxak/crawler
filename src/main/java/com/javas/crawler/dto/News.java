package com.javas.crawler.dto;

import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "crawling")
public class News {
  @Id
  @Nullable
  String _id;

  @Indexed(unique = false)
  @Nullable
  String title;

  @Nullable
  String content;

  @Nullable
  String summary;

  @Indexed(unique = false)
  @Nullable
  String mediaName;

  @Nullable
  String sid1;

  @Nullable
  String sid2;

  @Nullable
  String class1;

  @Nullable
  String class2;

  @Indexed(unique = true)
  @Nullable
  String uri;

  @Nullable
  String category;

  @Indexed(unique = false)
  @Nullable
  String rootDomain;

  @Nullable
  String pubDate;

  @Nullable
  String regDate;

  int readCheck;
}
