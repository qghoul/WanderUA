package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.AdvertisementImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementImageRepository extends JpaRepository <AdvertisementImage, Long> {

}
