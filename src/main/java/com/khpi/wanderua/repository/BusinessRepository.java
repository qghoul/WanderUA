package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Business;
import com.khpi.wanderua.entity.ReviewComplaint;
import com.khpi.wanderua.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    boolean existsBusinessByName(String name);
    Optional<Business> findByName(String name);
    Optional<Business> findByUser(User user);
}
