package com.kafu.sports.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String sport;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int points = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
    public int getPlayed() {
        return wins + draws + losses;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public int getGoalsFor() { return goalsFor; }
    public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }
}
