package com.kafu.sports.repository;

import com.kafu.sports.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeamId(Long teamId);
    List<Player> findAllByOrderByGoalsDesc();
}
