package com.bezkoder.spring.security.mongodb.domain.wordhunt;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wh_scores")
@CompoundIndexes({
  @CompoundIndex(name = "user_date_idx", def = "{ 'userId': 1, 'date': 1 }", unique = true),
  @CompoundIndex(name = "date_score_idx", def = "{ 'date': 1, 'score': -1 }")
})
public class WhScore {
  @Id
  private String id;
  private String userId;
  private String username;
  private LocalDate date;
  private int score;
  private int wordsFound;
  private OffsetDateTime updatedAt;
  private List<String> foundWords;

  public String getId() { return id; }
  public String getUserId() { return userId; }
  public String getUsername() { return username; }
  public LocalDate getDate() { return date; }
  public int getScore() { return score; }
  public int getWordsFound() { return wordsFound; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public List<String> getFoundWords() { return foundWords; }

  public void setUserId(String userId) { this.userId = userId; }
  public void setUsername(String username) { this.username = username; }
  public void setDate(LocalDate date) { this.date = date; }
  public void setScore(int score) { this.score = score; }
  public void setWordsFound(int wordsFound) { this.wordsFound = wordsFound; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  public void setFoundWords(List<String> foundWords) { this.foundWords = foundWords; }
}


