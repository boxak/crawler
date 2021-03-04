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
  String age;

  @Nullable
  String category;

  @Nullable
  String contents;

  @Nullable
  String likeCount;

  @Nullable
  String mediaName;

  @Nullable
  String modDate;

  int readCheck;

  @Nullable
  String regDate;

  @Nullable
  String rootDomain;

  @Nullable
  String sex;

  @Nullable
  String title;

  @Nullable
  String uri;

  @Nullable
  String wordCount;
}
