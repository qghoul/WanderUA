package com.khpi.wanderua.service;

import com.khpi.wanderua.dto.*;
import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.enums.*;
import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.enums.TourTheme;
import com.khpi.wanderua.repository.AdvertisementRepository;
import com.khpi.wanderua.repository.ThemeRepository;
import com.khpi.wanderua.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final AdvertisementImageService imageService;


    public AdvertisementResponse createAdvertisement(CreateAdvertisementRequest request,
                                                     List<MultipartFile> images,
                                                     String username) {

        log.info("Creating advertisement: {}", request.getTitle());

        if (images == null || images.isEmpty()) {
            throw new ValidationException("Зображення є обов'язковими для створення оголошення");
        }

        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Користувач не знайдений"));
        }

        Advertisement advertisement = createAdvertisementByType(request);
        advertisement.setUser(user);

        fillCommonFields(advertisement, request);

        advertisement = advertisementRepository.save(advertisement);

        if (images != null && !images.isEmpty()) {
            imageService.saveAdvertisementImages(advertisement, images);
        }

        log.info("Advertisement created with ID: {}", advertisement.getId());

        return mapToResponse(advertisement);
    }

    private Advertisement createAdvertisementByType(CreateAdvertisementRequest request) {
        switch (request.getAdvertisementType()) {
            case RESTAURANT:
                return createRestaurantAdvert((CreateRestaurantAdvertRequest) request);
            case TOUR:
                return createTourAdvert((CreateTourAdvertRequest) request);
            case ACCOMODATION:
                return createAccomodationAdvert((CreateAccomodationAdvertRequest) request);
            case ENTERTAINMENT:
                return createEntertainmentAdvert((CreateEntertainmentAdvertRequest) request);
            case PUBLIC_ATTRACTION:
                return createPublicAttractionAdvert((CreatePublicAttractionAdvertRequest) request);
            default:
                throw new IllegalArgumentException("Невідомий тип оголошення: " + request.getAdvertisementType());
        }
    }

    private RestaurantAdvert createRestaurantAdvert(CreateRestaurantAdvertRequest request) {
        RestaurantAdvert restaurant = new RestaurantAdvert();
        restaurant.setAddress(request.getAddress());
        restaurant.setPriceCategory(request.getPriceCategory());

        if (request.getSpecialOptions() != null) {
            restaurant.setVeganOptions(request.getSpecialOptions().contains("vegan"));
            restaurant.setHalalOptions(request.getSpecialOptions().contains("halal"));
        }

        return restaurant;
    }

    private TourAdvert createTourAdvert(CreateTourAdvertRequest request) {
        TourAdvert tour = new TourAdvert();
        tour.setTourType(request.getTourType());
        tour.setDuration(request.getDuration());
        tour.setTourPriceUsd(request.getPriceUsd());
        tour.setTourPriceUah(request.getPriceUah());

        if (request.getTourTheme() != null && !request.getTourTheme().isEmpty()) {
            Set<Theme> themes = convertStringListToThemes(request.getTourTheme());
            tour.setThemes(themes);
        }

        return tour;
    }
    private Set<Theme> convertStringListToThemes(List<String> themeStrings) {
        Set<Theme> themes = new HashSet<>();

        for (String themeString : themeStrings) {
            try {
                TourTheme tourTheme = TourTheme.valueOf(themeString.toUpperCase());

                Theme theme = themeRepository.findByTourTheme(tourTheme)
                        .orElseGet(() -> {
                            Theme newTheme = new Theme(tourTheme);
                            return themeRepository.save(newTheme);
                        });

                themes.add(theme);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown tour theme: {}", themeString);
            }
        }

        return themes;
    }

    private AccomodationAdvert createAccomodationAdvert(CreateAccomodationAdvertRequest request) {
        AccomodationAdvert accommodation = new AccomodationAdvert();
        accommodation.setAddress(request.getAddress());
        accommodation.setAccomodationType(request.getAccomodationType());

        return accommodation;
    }

    private EntertainmentAdvert createEntertainmentAdvert(CreateEntertainmentAdvertRequest request) {
        EntertainmentAdvert entertainment = new EntertainmentAdvert();
        entertainment.setAddress(request.getAddress());
        entertainment.setAgeCategory(request.getAgeCategory());

        return entertainment;
    }

    private PublicAttractionAdvert createPublicAttractionAdvert(CreatePublicAttractionAdvertRequest request) {
        PublicAttractionAdvert attraction = new PublicAttractionAdvert();
        attraction.setAddress(request.getAddress());
        attraction.setFreeVisit(request.getFreeVisit());

        return attraction;
    }

    private void fillCommonFields(Advertisement advertisement, CreateAdvertisementRequest request) {
        advertisement.setName(request.getTitle());
        advertisement.setDescription(request.getDescription());
        advertisement.setContact(request.getContacts());
        advertisement.setWebsiteUrl(request.getWebsite());

        if (request instanceof CreateRestaurantAdvertRequest) {
            CreateRestaurantAdvertRequest restaurantRequest = (CreateRestaurantAdvertRequest) request;
            advertisement.setCity(extractCityFromAddress(restaurantRequest.getAddress()));
        } else if (request instanceof CreateAccomodationAdvertRequest) {
            CreateAccomodationAdvertRequest accommodationRequest = (CreateAccomodationAdvertRequest) request;
            advertisement.setCity(extractCityFromAddress(accommodationRequest.getAddress()));
        } else if (request instanceof CreateEntertainmentAdvertRequest) {
            CreateEntertainmentAdvertRequest entertainmentRequest = (CreateEntertainmentAdvertRequest) request;
            advertisement.setCity(extractCityFromAddress(entertainmentRequest.getAddress()));
        } else if (request instanceof CreatePublicAttractionAdvertRequest) {
            CreatePublicAttractionAdvertRequest attractionRequest = (CreatePublicAttractionAdvertRequest) request;
            advertisement.setCity(extractCityFromAddress(attractionRequest.getAddress()));
        }

        if (request.getWorkdays() != null && !request.getWorkdays().trim().isEmpty()) {
            parseWorkingHours(advertisement, request.getWorkdays(), true);
        }

        if (request.getWeekends() != null && !request.getWeekends().trim().isEmpty()) {
            parseWorkingHours(advertisement, request.getWeekends(), false);
        }

        advertisement.setViews(0);
        advertisement.setClicks(0);
        advertisement.setRatingsCount(0);
        advertisement.setReviewAvgRating(0.0);
        advertisement.setPopularityScore(0.0);
    }
    private String extractCityFromAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "Не вказано";
        }

        String[] knownCities = {
                "Київ", "Львів", "Одеса", "Харків", "Дніпро", "Івано-Франківськ",
                "Тернопіль", "Ужгород", "Чернівці", "Запоріжжя", "Полтава",
                "Чернігів", "Миколаїв", "Херсон", "Кам'янець-Подільський", "Буковель"
        };

        for (String city : knownCities) {
            if (address.toLowerCase().contains(city.toLowerCase())) {
                return city;
            }
        }

        // If you cannot find a well-known city, return the first word from the address.
        String[] addressParts = address.split(",");
        if (addressParts.length > 0) {
            return addressParts[addressParts.length - 1].trim();
        }

        return "Не вказано";
    }
    private void parseWorkingHours(Advertisement advertisement, String timeRange, boolean isWeekday) {
        try {
            if (timeRange.contains(" - ")) {
                String[] times = timeRange.split(" - ");
                LocalTime openTime = LocalTime.parse(times[0].trim());
                LocalTime closeTime = LocalTime.parse(times[1].trim());

                if (isWeekday) {
                    advertisement.setWeekdayOpen(openTime);
                    advertisement.setWeekdayClose(closeTime);
                } else {
                    advertisement.setWeekendOpen(openTime);
                    advertisement.setWeekendClose(closeTime);
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse working hours: {}", timeRange, e);
        }
    }

    @Transactional
    public CatalogResponse getCatalogAdvertisements(CatalogFilterRequest filterRequest) {
        log.info("Getting catalog advertisements with filter: {}", filterRequest);

        Sort sort = createSortFromRequest(filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        Page<Advertisement> advertisementsPage = getFilteredAdvertisements(filterRequest, pageable);

        List<CatalogAdvertisementResponse> catalogAdvertisements = advertisementsPage.getContent()
                .stream()
                .map(this::mapToCatalogResponse)
                .collect(Collectors.toList());

        BigDecimal maxPrice = getMaxPriceInCatalog(filterRequest.getCity(), filterRequest.getCategory());

        Map<AdvertisementType, Long> typeStatistics = getTypeStatistics(filterRequest.getCity());

        return CatalogResponse.builder()
                .advertisements(catalogAdvertisements)
                .selectedCity(filterRequest.getCity())
                .maxPriceInCatalog(maxPrice != null ? maxPrice : BigDecimal.valueOf(100000))
                .totalElements((int) advertisementsPage.getTotalElements())
                .totalPages(advertisementsPage.getTotalPages())
                .currentPage(advertisementsPage.getNumber())
                .hasNext(advertisementsPage.hasNext())
                .hasPrevious(advertisementsPage.hasPrevious())
                .restaurantCount(typeStatistics.getOrDefault(AdvertisementType.RESTAURANT, 0L))
                .tourCount(typeStatistics.getOrDefault(AdvertisementType.TOUR, 0L))
                .accommodationCount(typeStatistics.getOrDefault(AdvertisementType.ACCOMODATION, 0L))
                .entertainmentCount(typeStatistics.getOrDefault(AdvertisementType.ENTERTAINMENT, 0L))
                .attractionCount(typeStatistics.getOrDefault(AdvertisementType.PUBLIC_ATTRACTION, 0L))
                .build();
    }

    private Sort createSortFromRequest(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "rating":
                return Sort.by(Sort.Direction.DESC, "review_avg_rating", "ratings_count");
            case "newest":
                return Sort.by(Sort.Direction.DESC, "created_at");
            case "price":
                return Sort.by(Sort.Direction.ASC, "tour_price_uah");
            case "popular":
            default:
                return Sort.by(Sort.Direction.DESC, "popularity_score", "views");
        }
    }

    private Page<Advertisement> getFilteredAdvertisements(CatalogFilterRequest filterRequest, Pageable pageable) {
        String city = filterRequest.getCity();
        String advertType = filterRequest.getCategory() != null ?
                mapAdvertisementTypeToDiscriminatorValue(filterRequest.getCategory()) : null;

        return advertisementRepository.findWithFilters(
                city,
                advertType,
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                pageable);
    }






    private String mapAdvertisementTypeToDiscriminatorValue(AdvertisementType type) {
        switch (type) {
            case RESTAURANT: return "RESTAURANT";
            case TOUR: return "TOUR";
            case ACCOMODATION: return "ACCOMODATION";
            case ENTERTAINMENT: return "ENTERTAINMENT";
            case PUBLIC_ATTRACTION: return "PUBLIC_ATTRACTION";
            default: return type.name();
        }
    }

    private BigDecimal getMaxPriceInCatalog(String city, AdvertisementType category) {
        if (city != null && !city.trim().isEmpty()) {
            return advertisementRepository.findMaxPriceInCity(city);
        } else {
            return advertisementRepository.findMaxPrice();
        }
    }
    private Map<AdvertisementType, Long> getTypeStatistics(String city) {
        Map<AdvertisementType, Long> statistics = new HashMap<>();

        if (city != null && !city.trim().isEmpty()) {
            statistics.put(AdvertisementType.RESTAURANT,
                    advertisementRepository.countByAdvertisementTypeAndCity("RESTAURANT", city));
            statistics.put(AdvertisementType.TOUR,
                    advertisementRepository.countByAdvertisementTypeAndCity("TOUR", city));
            statistics.put(AdvertisementType.ACCOMODATION,
                    advertisementRepository.countByAdvertisementTypeAndCity("ACCOMODATION", city));
            statistics.put(AdvertisementType.ENTERTAINMENT,
                    advertisementRepository.countByAdvertisementTypeAndCity("ENTERTAINMENT", city));
            statistics.put(AdvertisementType.PUBLIC_ATTRACTION,
                    advertisementRepository.countByAdvertisementTypeAndCity("PUBLIC_ATTRACTION", city));
        } else {
            statistics.put(AdvertisementType.RESTAURANT,
                    advertisementRepository.countByAdvertisementType("RESTAURANT"));
            statistics.put(AdvertisementType.TOUR,
                    advertisementRepository.countByAdvertisementType("TOUR"));
            statistics.put(AdvertisementType.ACCOMODATION,
                    advertisementRepository.countByAdvertisementType("ACCOMODATION"));
            statistics.put(AdvertisementType.ENTERTAINMENT,
                    advertisementRepository.countByAdvertisementType("ENTERTAINMENT"));
            statistics.put(AdvertisementType.PUBLIC_ATTRACTION,
                    advertisementRepository.countByAdvertisementType("PUBLIC_ATTRACTION"));
        }

        return statistics;
    }

    private CatalogAdvertisementResponse mapToCatalogResponse(Advertisement advertisement) {
        List<String> imageUrls = advertisement.getImages().stream()
                .map(img -> "/uploads/" + img.getName())
                .collect(Collectors.toList());

        String mainImageUrl = imageUrls.isEmpty() ? "/images/no-image.jpg" : imageUrls.get(0);

        Map<String, Object> typeSpecificData = new HashMap<>();
        BigDecimal priceUah = null;
        BigDecimal priceUsd = null;
        String priceDisplay = "";

        if (advertisement instanceof RestaurantAdvert) {
            RestaurantAdvert restaurant = (RestaurantAdvert) advertisement;
            typeSpecificData.put("priceCategory", restaurant.getPriceCategory());
            typeSpecificData.put("veganOptions", restaurant.getVeganOptions());
            typeSpecificData.put("halalOptions", restaurant.getHalalOptions());
            priceDisplay = formatPriceCategory(restaurant.getPriceCategory());

            List<String> specialOptions = new ArrayList<>();
            if (restaurant.getVeganOptions() != null && restaurant.getVeganOptions()) {
                specialOptions.add("Vegan options");
            }
            if (restaurant.getHalalOptions() != null && restaurant.getHalalOptions()) {
                specialOptions.add("Halal options");
            }
            if (!specialOptions.isEmpty()) {
                typeSpecificData.put("specialOptionsDisplay", String.join(", ", specialOptions));
            }

        } else if (advertisement instanceof TourAdvert) {
            TourAdvert tour = (TourAdvert) advertisement;
            typeSpecificData.put("tourType", tour.getTourType());

            if (tour.getThemes() != null && !tour.getThemes().isEmpty()) {
                log.debug("Tour {} has themes: {}", tour.getId(), tour.getThemes().size());

                Set<String> themeNames = tour.getThemes().stream()
                        .filter(Objects::nonNull)
                        .map(theme -> {
                            log.debug("Processing theme: {}", theme.getTourTheme());
                            return theme.getTourTheme().name();
                        })
                        .collect(Collectors.toSet());

                typeSpecificData.put("themes", themeNames);
                log.debug("Themes added to typeSpecificData: {}", themeNames);
            } else {
                log.debug("Tour {} has no themes", tour.getId());
                typeSpecificData.put("themes", new HashSet<>());
            }

            typeSpecificData.put("duration", tour.getDuration());

            priceUah = tour.getTourPriceUah();
            priceUsd = tour.getTourPriceUsd();

            // Форматируем цену
            StringBuilder priceBuilder = new StringBuilder();
            if (tour.getTourPriceUsd() != null) {
                priceBuilder.append("$").append(tour.getTourPriceUsd());
            }
            if (tour.getTourPriceUah() != null) {
                if (priceBuilder.length() > 0) priceBuilder.append(" / ");
                priceBuilder.append("₴").append(tour.getTourPriceUah());
            }
            priceDisplay = priceBuilder.length() > 0 ? priceBuilder.toString() : "";

        } else if (advertisement instanceof AccomodationAdvert) {
            AccomodationAdvert accommodation = (AccomodationAdvert) advertisement;
            typeSpecificData.put("accomodationType", accommodation.getAccomodationType());
            typeSpecificData.put("starsQuality", accommodation.getStarsQuality());

        } else if (advertisement instanceof EntertainmentAdvert) {
            EntertainmentAdvert entertainment = (EntertainmentAdvert) advertisement;
            typeSpecificData.put("ageCategory", entertainment.getAgeCategory());

        } else if (advertisement instanceof PublicAttractionAdvert) {
            PublicAttractionAdvert attraction = (PublicAttractionAdvert) advertisement;
            typeSpecificData.put("freeVisit", attraction.getFreeVisit());
            priceDisplay = attraction.getFreeVisit() ? "Безкоштовно" : "Платно";
        }

        String workingHours = formatWorkingHours(
                advertisement.getWeekdayOpen(), advertisement.getWeekdayClose(),
                advertisement.getWeekendOpen(), advertisement.getWeekendClose()
        );

        return CatalogAdvertisementResponse.builder()
                .id(advertisement.getId())
                .title(advertisement.getName())
                .description(advertisement.getDescription())
                .city(advertisement.getCity())
                .address(advertisement.getAddress())
                .advertisementType(advertisement.getAdvertisementType())
                .mainImageUrl(mainImageUrl)
                .imageUrls(imageUrls)
                .reviewAvgRating(advertisement.getReviewAvgRating())
                .ratingsCount(advertisement.getRatingsCount())
                .priceUah(priceUah)
                .priceUsd(priceUsd)
                .priceDisplay(priceDisplay)
                .workingHours(workingHours)
                .popularityScore(advertisement.getPopularityScore())
                .typeSpecificData(typeSpecificData)
                .createdAt(advertisement.getCreatedAt())
                .build();
    }

    private String formatWorkingHours(LocalTime weekdayOpen, LocalTime weekdayClose,
                                      LocalTime weekendOpen, LocalTime weekendClose) {
        if (weekdayOpen == null && weekdayClose == null &&
                weekendOpen == null && weekendClose == null) {
            return "Час роботи не вказано";
        }

        StringBuilder sb = new StringBuilder();
        if (weekdayOpen != null && weekdayClose != null) {
            sb.append("Пн-Пт: ").append(weekdayOpen).append("-").append(weekdayClose);
        }
        if (weekendOpen != null && weekendClose != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Сб-Нд: ").append(weekendOpen).append("-").append(weekendClose);
        }

        return sb.length() > 0 ? sb.toString() : "Час роботи не вказано";
    }

    @Transactional
    public Page<AdvertisementResponse> getAdvertisements(String category, Pageable pageable) {
        Page<Advertisement> advertisements;

        if (category != null && !category.trim().isEmpty()) {
            String advertType = mapCategoryToAdvertType(category);
            advertisements = advertisementRepository.findByAdvertisementType(advertType, pageable);
        } else {
            advertisements = advertisementRepository.findByIsActiveTrue(pageable);
        }

        return advertisements.map(this::mapToResponse);
    }
    @Transactional
    public Page<AdvertisementResponse> getUserAdvertisements(Long userId, Pageable pageable) {
        Page<Advertisement> advertisements = advertisementRepository.findByUserId(userId, pageable);
        return advertisements.map(this::mapToResponse);
    }
    @Transactional
    public AdvertisementResponse getAdvertisementById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Оголошення не знайдено"));

        advertisement.setViews(advertisement.getViews() + 1);
        advertisementRepository.save(advertisement);

        return mapToResponse(advertisement);
    }

    private String mapCategoryToAdvertType(String category) {
        switch (category.toLowerCase()) {
            case "restaurant": return "RESTAURANT";
            case "tour": return "TOUR";
            case "accomodation": return "ACCOMODATION";
            case "entertainment": return "ENTERTAINMENT";
            case "public_attraction": return "PUBLIC_ATTRACTION";
            default: throw new IllegalArgumentException("Невідома категорія: " + category);
        }
    }
    private AdvertisementResponse mapToResponse(Advertisement advertisement) {
        Map<String, Object> additionalData = new HashMap<>();

        if (advertisement instanceof RestaurantAdvert) {
            RestaurantAdvert restaurant = (RestaurantAdvert) advertisement;
            additionalData.put("address", restaurant.getAddress());
            additionalData.put("priceCategory", restaurant.getPriceCategory());
            additionalData.put("veganOptions", restaurant.getVeganOptions());
            additionalData.put("halalOptions", restaurant.getHalalOptions());
        } else if (advertisement instanceof TourAdvert) {
            TourAdvert tour = (TourAdvert) advertisement;
            additionalData.put("tourType", tour.getTourType());
            if (tour.getThemes() != null && !tour.getThemes().isEmpty()) {
                Set<String> themeNames = tour.getThemes().stream()
                        .map(theme -> theme.getTourTheme().name())
                        .collect(Collectors.toSet());
                additionalData.put("themes", themeNames);
            }
            additionalData.put("duration", tour.getDuration());
            additionalData.put("priceUsd", tour.getTourPriceUsd());
            additionalData.put("priceUah", tour.getTourPriceUah());
        } else if (advertisement instanceof AccomodationAdvert) {
            AccomodationAdvert accommodation = (AccomodationAdvert) advertisement;
            additionalData.put("address", accommodation.getAddress());
            additionalData.put("accomodationType", accommodation.getAccomodationType());
        } else if (advertisement instanceof EntertainmentAdvert) {
            EntertainmentAdvert entertainment = (EntertainmentAdvert) advertisement;
            additionalData.put("address", entertainment.getAddress());
            additionalData.put("ageCategory", entertainment.getAgeCategory());
        } else if (advertisement instanceof PublicAttractionAdvert) {
            PublicAttractionAdvert attraction = (PublicAttractionAdvert) advertisement;
            additionalData.put("address", attraction.getAddress());
            additionalData.put("freeVisit", attraction.getFreeVisit());
        }

        return AdvertisementResponse.builder()
                .id(advertisement.getId())
                .title(advertisement.getName())
                //.category(mapTypeToCategory(advertisement.getAdvertisementType()))
                .description(advertisement.getDescription())
                .contacts(advertisement.getContact())
                .website(advertisement.getWebsiteUrl())
                .advertisementType(advertisement.getAdvertisementType())
                .additionalData(additionalData)
                .build();
    }

    private String mapTypeToCategory(AdvertisementType type) {
        switch (type) {
            case RESTAURANT: return "restaurant";
            case TOUR: return "tour";
            case ACCOMODATION: return "accomodation";
            case ENTERTAINMENT: return "entertainment";
            case PUBLIC_ATTRACTION: return "public_attraction";
            default: return "unknown";
        }
    }


    @Transactional
    public AdvertisementDetailResponse getAdvertisementDetailById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Оголошення не знайдено"));

        advertisement.setViews(advertisement.getViews() + 1);
        advertisementRepository.save(advertisement);

        return mapToDetailResponse(advertisement);
    }

    private AdvertisementDetailResponse mapToDetailResponse(Advertisement advertisement) {
        Map<String, Object> additionalData = new HashMap<>();

        List<String> imageUrls = advertisement.getImages().stream()
                .map(img -> "/uploads/" + img.getName())
                .collect(Collectors.toList());


        if (advertisement instanceof RestaurantAdvert) {
            RestaurantAdvert restaurant = (RestaurantAdvert) advertisement;
            additionalData.put("address", restaurant.getAddress());
            additionalData.put("priceCategory", restaurant.getPriceCategory());
            additionalData.put("veganOptions", restaurant.getVeganOptions());
            additionalData.put("halalOptions", restaurant.getHalalOptions());

            String priceCategoryDisplay = formatPriceCategory(restaurant.getPriceCategory());
            additionalData.put("priceCategoryDisplay", priceCategoryDisplay);

            // Форматируем специальные опции
            List<String> specialOptions = new ArrayList<>();
            if (restaurant.getVeganOptions() != null && restaurant.getVeganOptions()) {
                specialOptions.add("Vegan options");
            }
            if (restaurant.getHalalOptions() != null && restaurant.getHalalOptions()) {
                specialOptions.add("Halal options");
            }
            additionalData.put("specialOptionsDisplay", String.join(", ", specialOptions));

        } else if (advertisement instanceof TourAdvert) {
            TourAdvert tour = (TourAdvert) advertisement;
            additionalData.put("tourType", tour.getTourType());
            if (tour.getThemes() != null && !tour.getThemes().isEmpty()) {
                Set<String> themeNames = tour.getThemes().stream()
                        .map(theme -> theme.getTourTheme().name())
                        .collect(Collectors.toSet());
                additionalData.put("themes", themeNames);
            }
            additionalData.put("duration", tour.getDuration());
            additionalData.put("priceUsd", tour.getTourPriceUsd());
            additionalData.put("priceUah", tour.getTourPriceUah());

            if (tour.getTourPriceUsd() != null || tour.getTourPriceUah() != null) {
                StringBuilder priceDisplay = new StringBuilder();
                if (tour.getTourPriceUsd() != null) {
                    priceDisplay.append("$").append(tour.getTourPriceUsd());
                }
                if (tour.getTourPriceUah() != null) {
                    if (priceDisplay.length() > 0) priceDisplay.append(" / ");
                    priceDisplay.append(tour.getTourPriceUah()).append(" грн");
                }
                additionalData.put("priceDisplay", priceDisplay.toString());
            }

            if (tour.getDuration() != null) {
                additionalData.put("durationDisplay", tour.getDuration() + " годин(и)");
            }

        } else if (advertisement instanceof AccomodationAdvert) {
            AccomodationAdvert accommodation = (AccomodationAdvert) advertisement;
            additionalData.put("address", accommodation.getAddress());
            additionalData.put("accomodationType", accommodation.getAccomodationType());
            additionalData.put("accomodationTypeDisplay", formatAccomodationType(accommodation.getAccomodationType()));

        } else if (advertisement instanceof EntertainmentAdvert) {
            EntertainmentAdvert entertainment = (EntertainmentAdvert) advertisement;
            additionalData.put("address", entertainment.getAddress());
            additionalData.put("ageCategory", entertainment.getAgeCategory());
            additionalData.put("ageCategoryDisplay", formatAgeCategory(entertainment.getAgeCategory()));

        } else if (advertisement instanceof PublicAttractionAdvert) {
            PublicAttractionAdvert attraction = (PublicAttractionAdvert) advertisement;
            additionalData.put("address", attraction.getAddress());
            additionalData.put("freeVisit", attraction.getFreeVisit());
            additionalData.put("freeVisitDisplay", attraction.getFreeVisit() ? "Безкоштовно" : "Платно");
        }

        return AdvertisementDetailResponse.builder()
                .id(advertisement.getId())
                .title(advertisement.getName())
                .description(advertisement.getDescription())
                .contacts(advertisement.getContact())
                .website(advertisement.getWebsiteUrl())
                .advertisementType(advertisement.getAdvertisementType())
                .address(advertisement.getAddress())
                .weekdayOpen(advertisement.getWeekdayOpen())
                .weekdayClose(advertisement.getWeekdayClose())
                .weekendOpen(advertisement.getWeekendOpen())
                .weekendClose(advertisement.getWeekendClose())
                .reviewAvgRating(advertisement.getReviewAvgRating())
                .ratingsCount(advertisement.getRatingsCount())
                .views(advertisement.getViews())
                .clicks(advertisement.getClicks())
                .popularityScore(advertisement.getPopularityScore())
                .familyFriendly(advertisement.getFamilyFriendly())
                .imageUrls(imageUrls)
                .additionalData(additionalData)
                .creatorName(advertisement.getUser() != null ? advertisement.getUser().getUsername() : "Невідомо")
                .isVerifiedBusiness(advertisement.getUser() != null ? advertisement.getUser().isBusinessVerified() : false)
                .createdAt(advertisement.getCreatedAt())
                .build();
    }

    private String formatPriceCategory(PriceCategory category) {
        if (category == null) return "";
        switch (category) {
            case CHEAP: return "$ Бюджетна";
            case MID: return "$$ Середня";
            case LUXURY: return "$$$ Люкс";
            default: return category.toString();
        }
    }

    private String formatAccomodationType(AccomodationType type) {
        if (type == null) return "";
        switch (type) {
            case HOTEL: return "Готель";
            case MOTEL: return "Мотель";
            case AGENCY: return "Агенство нерухомості";
            case OTHER: return "Інше";
            default: return type.toString();
        }
    }

    private String formatAgeCategory(AgeCategory category) {
        if (category == null) return "";
        switch (category) {
            case KIDS: return "Діти";
            case TEENS: return "Підлітки";
            case ADULTS: return "Дорослі";
            case ONLY_ADULTS: return "Лише для дорослих";
            default: return category.toString();
        }
    }
}