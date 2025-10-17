package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.AdvertisementComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementComplaintRepository extends JpaRepository<AdvertisementComplaint, Long> {
    List<AdvertisementComplaint> findByAdvertisementId(Long advertisementId);

    List<AdvertisementComplaint> findByResolved(boolean resolved);

    List<AdvertisementComplaint> findByConfirmed(boolean confirmed);

    Optional<AdvertisementComplaint> findByUserIdAndAdvertisementId(Long userId, Long advertisementId);

    boolean existsByUserIdAndAdvertisementId(Long userId, Long advertisementId);

    List<AdvertisementComplaint> findByUserId(Long userId);

}
