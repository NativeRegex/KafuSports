package com.kafu.sports.repository;

import com.kafu.sports.entity.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixtureRepository extends JpaRepository<Fixture, Long> {
}
