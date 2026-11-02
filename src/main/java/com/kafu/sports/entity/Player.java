package com.kafu.sports.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position; // GK, DEF, MID, FWD
    private int jerseyNumber;
    private int goals = 0;
    private int assists = 0;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public int getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber) { this.jerseyNumber = jerseyNumber; }
    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }
    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
}
