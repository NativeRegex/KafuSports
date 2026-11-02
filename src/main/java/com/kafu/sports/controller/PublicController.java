package com.kafu.sports.controller;

import com.kafu.sports.entity.Fixture;
import com.kafu.sports.entity.Player;
import com.kafu.sports.entity.Result;
import com.kafu.sports.entity.Team;
import com.kafu.sports.repository.FixtureRepository;
import com.kafu.sports.repository.PlayerRepository;
import com.kafu.sports.repository.ResultRepository;
import com.kafu.sports.repository.TeamRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PublicController {

    private final TeamRepository teamRepo;
    private final FixtureRepository fixtureRepo;
    private final ResultRepository resultRepo;
    private final PlayerRepository playerRepo;

    public PublicController(TeamRepository teamRepo, FixtureRepository fixtureRepo,
                            ResultRepository resultRepo, PlayerRepository playerRepo) {
        this.teamRepo = teamRepo;
        this.fixtureRepo = fixtureRepo;
        this.resultRepo = resultRepo;
        this.playerRepo = playerRepo;
    }

    // ---------- HOME ----------
    @GetMapping("/")
    public String home(Model model) {
        List<Team> teams = teamRepo.findAll();
        teams.sort((a, b) -> {
            int cmp = Integer.compare(b.getPoints(), a.getPoints());
            if (cmp != 0) return cmp;
            return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
        });

        List<Fixture> upcoming = fixtureRepo.findAll().stream()
                .filter(f -> "SCHEDULED".equals(f.getStatus()))
                .limit(3).toList();

        model.addAttribute("topTeams", teams);
        model.addAttribute("nextFixtures", upcoming);
        model.addAttribute("recentResults", resultRepo.findAll().stream().limit(3).toList());
        return "index";
    }

    // ---------- LEAGUE ----------
    @GetMapping("/league")
    public String leagueTable(Model model) {
        List<Team> teams = teamRepo.findAll();
        teams.sort((a, b) -> {
            int cmp = Integer.compare(b.getPoints(), a.getPoints());
            if (cmp != 0) return cmp;
            return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
        });
        model.addAttribute("teams", teams);
        return "league";
    }

    // ---------- FIXTURES ----------
    @GetMapping("/fixtures")
    public String publicFixtures(Model model) {
        model.addAttribute("fixtures", fixtureRepo.findAll());
        return "fixtures";
    }

    // ---------- RESULTS ----------
    @GetMapping("/results")
    public String publicResults(Model model) {
        model.addAttribute("results", resultRepo.findAll());
        return "results";
    }

    // ---------- TEAMS LIST ----------
    @GetMapping("/teams")
    public String teamsPage(Model model) {
        List<Team> teams = teamRepo.findAll();
        teams.sort((a, b) -> {
            int cmp = Integer.compare(b.getPoints(), a.getPoints());
            if (cmp != 0) return cmp;
            return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
        });
        model.addAttribute("teams", teams);
        return "teams";
    }

    // ---------- TEAM DETAIL ----------
    @GetMapping("/teams/{id}")
    public String teamDetail(@PathVariable Long id, Model model) {
        Team team = teamRepo.findById(id).orElseThrow();

        List<Fixture> teamFixtures = fixtureRepo.findAll().stream()
                .filter(f -> f.getHomeTeam().getId().equals(id) || f.getAwayTeam().getId().equals(id))
                .filter(f -> "SCHEDULED".equals(f.getStatus()))
                .toList();

        List<Result> teamResults = resultRepo.findAll().stream()
                .filter(r -> r.getHomeTeam().getId().equals(id) || r.getAwayTeam().getId().equals(id))
                .toList();

        List<Player> squad = playerRepo.findByTeamId(id);

        model.addAttribute("team", team);
        model.addAttribute("fixtures", teamFixtures);
        model.addAttribute("results", teamResults);
        model.addAttribute("squad", squad);
        return "team-detail";
    }

    // ---------- PLAYERS ----------
    @GetMapping("/players")
    public String playersPage(Model model) {
        model.addAttribute("topScorers", playerRepo.findAllByOrderByGoalsDesc());
        return "players";
    }

    // ---------- STATS ----------
    @GetMapping("/stats")
    public String statsPage(Model model) {
        List<Team> teams = teamRepo.findAll();
        teams.sort((a, b) -> {
            int cmp = Integer.compare(b.getPoints(), a.getPoints());
            if (cmp != 0) return cmp;
            return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
        });

        List<Player> scorers = playerRepo.findAllByOrderByGoalsDesc();

        int totalGoals = resultRepo.findAll().stream()
                .mapToInt(r -> r.getHomeScore() + r.getAwayScore()).sum();
        int totalMatches = resultRepo.findAll().size();

        model.addAttribute("teams", teams);
        model.addAttribute("topScorers", scorers.stream().limit(10).toList());
        model.addAttribute("totalGoals", totalGoals);
        model.addAttribute("totalMatches", totalMatches);
        return "stats";
    }
}
