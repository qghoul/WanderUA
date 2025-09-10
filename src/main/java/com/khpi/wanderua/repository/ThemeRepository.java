package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Theme;
import com.khpi.wanderua.enums.TourTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findByTourTheme(TourTheme tourTheme);
}