package com.bezkoder.spring.security.mongodb.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bezkoder.spring.security.mongodb.domain.wordhunt.WhDailySeed;
import com.bezkoder.spring.security.mongodb.domain.wordhunt.WhScore;
import com.bezkoder.spring.security.mongodb.repository.WhDailySeedRepository;
import com.bezkoder.spring.security.mongodb.repository.WhScoreRepository;

@Service
public class WordHuntService {
  private final WhDailySeedRepository seedRepository;
  private final WhScoreRepository scoreRepository;

  private static final int DEFAULT_GRID_SIZE = 4;
  private static final String ALPHABET = "EEEEEEEEEEEEAAAAAAAAAIIIIIIIIIIOOOOOOOOONNNNNNRRRRRRTTTTTLLLLSSSSUUUUDDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ"; // biased frequency

  private static final Set<String> DICTIONARY = new HashSet<>(Arrays.asList(
    // minimal demo dictionary – replace with a larger list as needed
    "CAT", "DOG", "TREE", "READ", "WORD", "HUNT", "GAME", "SCORE", "GRID", "JAVA",
    "CODE", "MONGO", "SPRING", "BEAN", "USER", "ROLE", "WAVE", "TONE", "STONE", "NOTE",
    "HONEY", "RING", "RANGE", "RANGER", "MEAN", "MEANT", "GO", "GOAL", "GOALS", "TEA",
    "EAT", "ATE", "RATE", "RATED", "RATES", "STAR", "START", "STARTS", "TAR", "ART"
  ));

  @Autowired
  public WordHuntService(WhDailySeedRepository seedRepository, WhScoreRepository scoreRepository) {
    this.seedRepository = seedRepository;
    this.scoreRepository = scoreRepository;
  }

  public DailyResult getDaily(LocalDate date, Integer sizeOpt) {
    int size = sizeOpt == null ? DEFAULT_GRID_SIZE : Math.max(3, Math.min(6, sizeOpt));
    WhDailySeed seed = seedRepository.findByDate(date).orElseGet(() -> {
      long newSeed = computeDeterministicSeed(date);
      WhDailySeed created = new WhDailySeed(date, newSeed);
      return seedRepository.save(created);
    });
    char[][] grid = generateGrid(seed.getSeed(), size);
    return new DailyResult(seed.getSeed(), size, grid);
  }

  public SubmitResult submit(LocalDate date, String userId, String username, List<String> words) {
    if (words == null) {
      words = Collections.emptyList();
    }
    WhScore existing = scoreRepository.findByUserIdAndDate(userId, date).orElse(null);
    if (existing != null) {
      // simple daily cap: one submission per day
      return SubmitResult.rejected("Already submitted today.", existing.getScore(), existing.getWordsFound(), existing.getFoundWords());
    }

    DailyResult daily = getDaily(date, DEFAULT_GRID_SIZE);
    Set<String> unique = words.stream()
        .filter(Objects::nonNull)
        .map(s -> s.trim().toUpperCase())
        .filter(s -> s.length() >= 3)
        .collect(Collectors.toCollection(HashSet::new));

    List<String> accepted = new ArrayList<>();
    int total = 0;
    for (String w : unique) {
      if (isValidWordOnGrid(w, daily.grid) && DICTIONARY.contains(w)) {
        accepted.add(w);
        total += scoreFor(w);
      }
    }

    WhScore score = new WhScore();
    score.setUserId(userId);
    score.setUsername(username);
    score.setDate(date);
    score.setScore(total);
    score.setWordsFound(accepted.size());
    score.setFoundWords(accepted);
    score.setUpdatedAt(OffsetDateTime.now());
    scoreRepository.save(score);

    return SubmitResult.accepted(total, accepted.size(), accepted);
  }

  public List<WhScore> leaderboard(LocalDate date, int limit) {
    int capped = Math.max(1, Math.min(200, limit));
    return scoreRepository.findByDateOrderByScoreDesc(date, PageRequest.of(0, capped));
  }

  public WhScore me(LocalDate date, String userId) {
    return scoreRepository.findByUserIdAndDate(userId, date).orElse(null);
  }

  private static long computeDeterministicSeed(LocalDate date) {
    // Simple seed from date components to keep deterministic
    return date.getYear() * 10000L + date.getMonthValue() * 100L + date.getDayOfMonth();
  }

  private static char[][] generateGrid(long seed, int size) {
    Random rand = new Random(seed);
    char[][] grid = new char[size][size];
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        int idx = rand.nextInt(ALPHABET.length());
        grid[r][c] = ALPHABET.charAt(idx);
      }
    }
    return grid;
  }

  private static boolean isValidWordOnGrid(String word, char[][] grid) {
    int n = grid.length;
    boolean[][] visited = new boolean[n][n];
    for (int r = 0; r < n; r++) {
      for (int c = 0; c < n; c++) {
        if (grid[r][c] == word.charAt(0)) {
          if (dfs(word, 0, r, c, grid, visited)) return true;
        }
      }
    }
    return false;
  }

  private static boolean dfs(String word, int idx, int r, int c, char[][] grid, boolean[][] visited) {
    if (idx == word.length()) return true;
    if (r < 0 || c < 0 || r >= grid.length || c >= grid.length) return false;
    if (visited[r][c]) return false;
    if (grid[r][c] != word.charAt(idx)) return false;
    visited[r][c] = true;
    // explore 8 directions
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) continue;
        if (dfs(word, idx + 1, r + dr, c + dc, grid, visited)) {
          visited[r][c] = false;
          return true;
        }
      }
    }
    visited[r][c] = false;
    return idx + 1 == word.length();
  }

  private static int scoreFor(String word) {
    int len = word.length();
    if (len <= 2) return 0;
    if (len <= 4) return 1;
    if (len == 5) return 2;
    if (len == 6) return 3;
    if (len == 7) return 5;
    return 11;
  }

  public static class DailyResult {
    public final long seed;
    public final int size;
    public final char[][] grid;
    public DailyResult(long seed, int size, char[][] grid) {
      this.seed = seed;
      this.size = size;
      this.grid = grid;
    }
  }

  public static class SubmitResult {
    public final boolean accepted;
    public final String message;
    public final int score;
    public final int wordsFound;
    public final List<String> words;

    private SubmitResult(boolean accepted, String message, int score, int wordsFound, List<String> words) {
      this.accepted = accepted;
      this.message = message;
      this.score = score;
      this.wordsFound = wordsFound;
      this.words = words;
    }

    public static SubmitResult accepted(int score, int wordsFound, List<String> words) {
      return new SubmitResult(true, "OK", score, wordsFound, words);
    }

    public static SubmitResult rejected(String message, int score, int wordsFound, List<String> words) {
      return new SubmitResult(false, message, score, wordsFound, words);
    }
  }
}


