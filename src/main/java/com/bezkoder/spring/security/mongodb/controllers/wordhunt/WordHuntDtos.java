package com.bezkoder.spring.security.mongodb.controllers.wordhunt;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WordHuntDtos {
  public static class DailyResponse {
    public long seed;
    public int size;
    public char[][] grid;
  }

  public static class SubmitRequest {
    @NotNull
    public String username;
    @NotNull
    public List<String> words;
  }

  public static class SubmitResponse {
    public boolean accepted;
    public String message;
    public int score;
    public int wordsFound;
    public List<String> words;
  }

  public static class LeaderboardQuery {
    @NotNull
    public LocalDate date;
    @Min(1)
    public int limit = 50;
  }

  public static class ScoreRow {
    public String username;
    public int score;
    public int wordsFound;
  }

  public static class LeaderboardResponse {
    public LocalDate date;
    public List<ScoreRow> top;
  }

  public static class MeResponse {
    public LocalDate date;
    public String username;
    public Integer score;
    public Integer wordsFound;
    public List<String> words;
  }
}


