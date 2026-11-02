package com.kafu.sports.repository;

import com.kafu.sports.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findBySportOrderByPointsDesc(String sport);
}
