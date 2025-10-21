package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Advertisement;
import com.khpi.wanderua.entity.TravelIdea;
import com.khpi.wanderua.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelIdeaRepository extends JpaRepository<TravelIdea, Long> {

    /**
     * Знайти всі TravelIdea користувача, відсортовані по ID (нові спочатку)
     */
    List<TravelIdea> findByUser(User user);

    /**
     * Знайти TravelIdea по ID та користувачу (для безпеки)
     */
    Optional<TravelIdea> findByIdAndUser(Long id, User user);


}
