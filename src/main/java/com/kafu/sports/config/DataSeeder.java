package com.kafu.sports.config;

import com.kafu.sports.entity.Team;
import com.kafu.sports.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(TeamRepository repository) {
        return args -> {
            // Only run if the database is empty
            if (repository.count() == 0) {
                Object[][] data = {
                    {"Young boys", 7, 6, 0, 1, 13, 2},
                    {"Wolves", 7, 5, 2, 0, 30, 4},
                    {"Juala wang", 7, 5, 2, 0, 13, 3},
                    {"Maganda chiefs", 7, 5, 1, 1, 13, 2},
                    {"Falcon fc", 6, 4, 1, 1, 12, 9},
                    {"Scit", 6, 4, 0, 2, 8, 6},
                    {"Alpha fc", 7, 4, 0, 3, 10, 9},
                    {"Kapenguria fc", 6, 3, 2, 1, 8, 4},
                    {"Newcastle united", 7, 2, 3, 2, 12, 8},
                    {"Aldai all-stars", 6, 3, 0, 3, 7, 9},
                    {"Atalanta rovers", 6, 2, 1, 3, 6, 7},
                    {"Sportive", 7, 1, 3, 3, 5, 7},
                    {"Staff", 5, 1, 1, 3, 5, 12},
                    {"Legends fc", 7, 1, 1, 5, 3, 11},
                    {"Bayern fc", 6, 1, 0, 5, 3, 14},
                    {"Crown rovers", 6, 1, 0, 5, 1, 17},
                    {"Emsa fc", 7, 0, 2, 5, 6, 22},
                    {"Bahari fc", 6, 0, 1, 5, 3, 12}
                };

                for (Object[] row : data) {
                    Team t = new Team();
                    t.setName((String) row[0]);
                    t.setSport("Men's Football");
                    
                    int played = (int) row[1];
                    int wins = (int) row[2];
                    int draws = (int) row[3];
                    int losses = (int) row[4];
                    int gf = (int) row[5];
                    int ga = (int) row[6];

                    t.setWins(wins);
                    t.setDraws(draws);
                    t.setLosses(losses);
                    t.setGoalsFor(gf);
                    t.setGoalsAgainst(ga);
                    
                    // Calculate points (Win = 3, Draw = 1)
                    t.setPoints((wins * 3) + draws);

                    repository.save(t);
                }
                System.out.println("✅ 18 Teams seeded successfully from the image!");
            }
        };
    }
}
