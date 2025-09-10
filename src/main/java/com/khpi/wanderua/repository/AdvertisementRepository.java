package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Advertisement;
import com.khpi.wanderua.enums.AdvertisementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository
        extends JpaRepository<Advertisement, Long> {

    Page<Advertisement> findAll(Pageable pageable);
    Advertisement findAdvertisementById(Long id);
    Optional<Advertisement> findById(Long id);

    @Query(value = "SELECT * FROM advertisements WHERE advert_type = :advertType", nativeQuery = true)
    Page<Advertisement> findByAdvertisementType(@Param("advertType") String advertType, Pageable pageable);


    @Query("SELECT a FROM Advertisement a WHERE a.user.id = :userId")
    Page<Advertisement> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM Advertisement a WHERE LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    Page<Advertisement> findByCityContainingIgnoreCase(@Param("city") String city, Pageable pageable);

    @Query(value = "SELECT * FROM advertisements " +
            "WHERE LOWER(city) LIKE LOWER(CONCAT('%', :city, '%')) " +
            "AND advert_type = :advertType " +
            "ORDER BY popularity_score DESC",
            countQuery = "SELECT COUNT(*) FROM advertisements " +
                    "WHERE LOWER(city) LIKE LOWER(CONCAT('%', :city, '%')) " +
                    "AND advert_type = :advertType",
            nativeQuery = true)
    Page<Advertisement> findByCityAndAdvertisementType(
            @Param("city") String city,
            @Param("advertType") String advertType,
            Pageable pageable);

    @Query(value = "SELECT MAX(tour_price_uah) FROM advertisements WHERE LOWER(city) LIKE LOWER(CONCAT('%', :city, '%')) AND advert_type = 'TOUR'",
            nativeQuery = true)
    BigDecimal findMaxPriceInCity(@Param("city") String city);

    @Query(value = "SELECT MAX(tour_price_uah) FROM advertisements WHERE advert_type = 'TOUR'",
            nativeQuery = true)
    BigDecimal findMaxPrice();

    @Query(value = "SELECT COUNT(*) FROM advertisements WHERE advert_type = :advertType AND LOWER(city) LIKE LOWER(CONCAT('%', :city, '%'))",
            nativeQuery = true)
    Long countByAdvertisementTypeAndCity(@Param("advertType") String advertType, @Param("city") String city);

    @Query(value = "SELECT COUNT(*) FROM advertisements WHERE advert_type = :advertType",
            nativeQuery = true)
    Long countByAdvertisementType(@Param("advertType") String advertType);


    @Query("SELECT a FROM Advertisement a WHERE " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%')) " +
            "ORDER BY a.popularityScore DESC, a.views DESC")
    Page<Advertisement> findPopularInCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT a FROM Advertisement a WHERE " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%')) AND " +
            "a.reviewAvgRating > 0 " +
            "ORDER BY a.reviewAvgRating DESC, a.ratingsCount DESC")
    Page<Advertisement> findTopRatedInCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT a FROM Advertisement a WHERE " +
            "LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%')) " +
            "ORDER BY a.createdAt DESC")
    Page<Advertisement> findNewestInCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT t FROM TourAdvert t WHERE " +
            "LOWER(t.city) LIKE LOWER(CONCAT('%', :city, '%')) AND " +
            "t.tourPriceUah BETWEEN :minPrice AND :maxPrice")
    Page<Advertisement> findToursByCityAndPriceRange(@Param("city") String city,
                                                     @Param("minPrice") BigDecimal minPrice,
                                                     @Param("maxPrice") BigDecimal maxPrice,
                                                     Pageable pageable);

    @Query("SELECT t FROM TourAdvert t WHERE t.tourPriceUah BETWEEN :minPrice AND :maxPrice")
    Page<Advertisement> findToursByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                              @Param("maxPrice") BigDecimal maxPrice,
                                              Pageable pageable);

    @Query(value = "SELECT * FROM advertisements WHERE " +
            "(:city IS NULL OR LOWER(city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:advertType IS NULL OR advert_type = :advertType) AND " +
            "(:minPrice IS NULL OR tour_price_uah >= :minPrice) AND " +
            "(:maxPrice IS NULL OR tour_price_uah <= :maxPrice)",
            countQuery = "SELECT COUNT(*) FROM advertisements WHERE " +
                    "(:city IS NULL OR LOWER(city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
                    "(:advertType IS NULL OR advert_type = :advertType) AND " +
                    "(:minPrice IS NULL OR tour_price_uah >= :minPrice) AND " +
                    "(:maxPrice IS NULL OR tour_price_uah <= :maxPrice)",
            nativeQuery = true)
    Page<Advertisement> findWithFilters(
            @Param("city") String city,
            @Param("advertType") String advertType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

}
