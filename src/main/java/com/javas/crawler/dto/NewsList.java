package com.javas.crawler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "news_list")
public class NewsList {
  @Id
  String _id;
  @Indexed(unique = true)
  String uri;
  String docStr;
}
