package com.kafu.sports.controller;

import com.kafu.sports.entity.*;
import com.kafu.sports.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TeamRepository teamRepo;
    private final FixtureRepository fixtureRepo;
    private final ResultRepository resultRepo;
    private final PlayerRepository playerRepo;

    public AdminController(TeamRepository teamRepo, FixtureRepository fixtureRepo,
                           ResultRepository resultRepo, PlayerRepository playerRepo) {
        this.teamRepo = teamRepo;
        this.fixtureRepo = fixtureRepo;
        this.resultRepo = resultRepo;
        this.playerRepo = playerRepo;
    }

    // ---------- TEAMS ----------
    @GetMapping("/teams")
    public String teamsPage(Model model) {
        model.addAttribute("teams", teamRepo.findAll());
        model.addAttribute("newTeam", new Team());
        return "admin-teams";
    }

    @PostMapping("/teams/add")
    public String addTeam(@ModelAttribute Team team) {
        teamRepo.save(team);
        return "redirect:/admin/teams";
    }

    @GetMapping("/teams/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamRepo.deleteById(id);
        return "redirect:/admin/teams";
    }

    // ---------- FIXTURES ----------
    @GetMapping("/fixtures")
    public String fixturesPage(Model model) {
        model.addAttribute("fixtures", fixtureRepo.findAll());
        model.addAttribute("teams", teamRepo.findAll());
        return "admin-fixtures";
    }

    @PostMapping("/fixtures/add")
    public String addFixture(@RequestParam Long homeTeamId,
                             @RequestParam Long awayTeamId,
                             @RequestParam String matchDate,
                             @RequestParam String venue) {
        Fixture f = new Fixture();
        f.setHomeTeam(teamRepo.findById(homeTeamId).orElseThrow());
        f.setAwayTeam(teamRepo.findById(awayTeamId).orElseThrow());
        f.setMatchDate(LocalDateTime.parse(matchDate));
        f.setVenue(venue);
        f.setStatus("SCHEDULED");
        fixtureRepo.save(f);
        return "redirect:/admin/fixtures";
    }

    @GetMapping("/fixtures/delete/{id}")
    public String deleteFixture(@PathVariable Long id) {
        fixtureRepo.deleteById(id);
        return "redirect:/admin/fixtures";
    }

    // ---------- RESULTS ----------
    @GetMapping("/results")
    public String resultsPage(Model model) {
        model.addAttribute("results", resultRepo.findAll());
        model.addAttribute("fixtures", fixtureRepo.findAll());
        return "admin-results";
    }

    @PostMapping("/results/add")
    public String addResult(@RequestParam Long fixtureId,
                            @RequestParam int homeScore,
                            @RequestParam int awayScore) {
        Fixture f = fixtureRepo.findById(fixtureId).orElseThrow();

        Result r = new Result();
        r.setHomeTeam(f.getHomeTeam());
        r.setAwayTeam(f.getAwayTeam());
        r.setHomeScore(homeScore);
        r.setAwayScore(awayScore);
        r.setMatchDate(f.getMatchDate());
        r.setVenue(f.getVenue());
        resultRepo.save(r);

        Team home = f.getHomeTeam();
        Team away = f.getAwayTeam();

        // Update goals for/against
        home.setGoalsFor(home.getGoalsFor() + homeScore);
        home.setGoalsAgainst(home.getGoalsAgainst() + awayScore);
        away.setGoalsFor(away.getGoalsFor() + awayScore);
        away.setGoalsAgainst(away.getGoalsAgainst() + homeScore);

        // Update wins/draws/losses/points
        if (homeScore > awayScore) {
            home.setWins(home.getWins() + 1); home.setPoints(home.getPoints() + 3);
            away.setLosses(away.getLosses() + 1);
        } else if (homeScore < awayScore) {
            away.setWins(away.getWins() + 1); away.setPoints(away.getPoints() + 3);
            home.setLosses(home.getLosses() + 1);
        } else {
            home.setDraws(home.getDraws() + 1); home.setPoints(home.getPoints() + 1);
            away.setDraws(away.getDraws() + 1); away.setPoints(away.getPoints() + 1);
        }

        teamRepo.save(home);
        teamRepo.save(away);

        f.setStatus("COMPLETED");
        f.setHomeScore(homeScore);
        f.setAwayScore(awayScore);
        fixtureRepo.save(f);

        return "redirect:/admin/results";
    }

    @GetMapping("/results/delete/{id}")
    public String deleteResult(@PathVariable Long id) {
        resultRepo.deleteById(id);
        return "redirect:/admin/results";
    }

    // ---------- PLAYERS ----------
    @GetMapping("/players")
    public String playersPage(Model model) {
        model.addAttribute("players", playerRepo.findAll());
        model.addAttribute("teams", teamRepo.findAll());
        return "admin-players";
    }

    @PostMapping("/players/add")
    public String addPlayer(@RequestParam String name,
                            @RequestParam String position,
                            @RequestParam int jerseyNumber,
                            @RequestParam Long teamId) {
        Player p = new Player();
        p.setName(name);
        p.setPosition(position);
        p.setJerseyNumber(jerseyNumber);
        p.setTeam(teamRepo.findById(teamId).orElseThrow());
        playerRepo.save(p);
        return "redirect:/admin/players";
    }

    @GetMapping("/players/delete/{id}")
    public String deletePlayer(@PathVariable Long id) {
        playerRepo.deleteById(id);
        return "redirect:/admin/players";
    }
}
