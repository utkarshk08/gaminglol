package com.bezkoder.spring.security.mongodb.domain.wordhunt;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wh_daily_seed")
public class WhDailySeed {
  @Id
  private String id;
  @Indexed(unique = true)
  private LocalDate date;
  private long seed;

  public WhDailySeed() {}

  public WhDailySeed(LocalDate date, long seed) {
    this.date = date;
    this.seed = seed;
  }

  public String getId() { return id; }
  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }
  public long getSeed() { return seed; }
  public void setSeed(long seed) { this.seed = seed; }
}


