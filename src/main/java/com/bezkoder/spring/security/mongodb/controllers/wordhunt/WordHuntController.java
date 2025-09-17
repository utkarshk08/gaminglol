package com.bezkoder.spring.security.mongodb.controllers.wordhunt;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.security.mongodb.controllers.wordhunt.WordHuntDtos.DailyResponse;
import com.bezkoder.spring.security.mongodb.controllers.wordhunt.WordHuntDtos.LeaderboardResponse;
import com.bezkoder.spring.security.mongodb.controllers.wordhunt.WordHuntDtos.MeResponse;
import com.bezkoder.spring.security.mongodb.controllers.wordhunt.WordHuntDtos.ScoreRow;
import com.bezkoder.spring.security.mongodb.controllers.wordhunt.WordHuntDtos.SubmitRequest;
import com.bezkoder.spring.security.mongodb.controllers.wordhunt.WordHuntDtos.SubmitResponse;
import com.bezkoder.spring.security.mongodb.service.WordHuntService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/wordhunt")
public class WordHuntController {
  @Autowired
  private WordHuntService service;

  @GetMapping("/daily")
  public ResponseEntity<DailyResponse> daily(@RequestParam(name = "date", required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                             LocalDate date,
                                             @RequestParam(name = "size", required = false) Integer size) {
    LocalDate d = date == null ? LocalDate.now() : date;
    var res = service.getDaily(d, size);
    DailyResponse dto = new DailyResponse();
    dto.seed = res.seed;
    dto.size = res.size;
    dto.grid = res.grid;
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/submit")
  public ResponseEntity<SubmitResponse> submit(@RequestParam(name = "date", required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                               LocalDate date,
                                               @RequestBody SubmitRequest request) {
    LocalDate d = date == null ? LocalDate.now() : date;
    String username = request.username != null ? request.username.trim() : "guest";
    var result = service.submit(d, username, username, request.words);
    SubmitResponse dto = new SubmitResponse();
    dto.accepted = result.accepted;
    dto.message = result.message;
    dto.score = result.score;
    dto.wordsFound = result.wordsFound;
    dto.words = result.words;
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/leaderboard")
  public ResponseEntity<LeaderboardResponse> leaderboard(@RequestParam(name = "date", required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                         LocalDate date,
                                                         @RequestParam(name = "limit", required = false, defaultValue = "50") int limit) {
    LocalDate d = date == null ? LocalDate.now() : date;
    var rows = service.leaderboard(d, limit);
    LeaderboardResponse resp = new LeaderboardResponse();
    resp.date = d;
    resp.top = rows.stream().map(s -> {
      ScoreRow r = new ScoreRow();
      r.username = s.getUsername();
      r.score = s.getScore();
      r.wordsFound = s.getWordsFound();
      return r;
    }).collect(Collectors.toList());
    return ResponseEntity.ok(resp);
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> me(@RequestParam(name = "date", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       LocalDate date,
                                       @RequestParam(name = "username") String username) {
    LocalDate d = date == null ? LocalDate.now() : date;
    var me = service.me(d, username);
    MeResponse resp = new MeResponse();
    resp.date = d;
    resp.username = username;
    if (me != null) {
      resp.score = me.getScore();
      resp.wordsFound = me.getWordsFound();
      resp.words = me.getFoundWords();
    }
    return ResponseEntity.ok(resp);
  }
}


