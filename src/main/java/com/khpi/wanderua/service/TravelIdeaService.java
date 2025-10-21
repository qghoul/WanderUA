package com.khpi.wanderua.service;

import com.khpi.wanderua.repository.*;
import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelIdeaService {

    private final AdvertisementService advertisementService;
    private final TravelIdeaRepository travelIdeaRepository;

    @Transactional
    public TravelIdeaDTO createTravelIdea(User user, TravelIdeaDTO travelIdeaDTO){

        TravelIdea travelIdea = new TravelIdea();
        travelIdea.setTitle(travelIdeaDTO.getTitle());
        travelIdea.setUser(user);
        travelIdea.setDescription(travelIdeaDTO.getDescription());

        TravelIdea saved = travelIdeaRepository.save(travelIdea);

        log.info("Travel idea created with id: {}", saved.getId());

        return mapToTravelIdeaResponse(saved);
    }
    @Transactional
    public void deleteTravelIdea(User user, Long id){
        TravelIdea travelIdea = travelIdeaRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("TravelIdea not found"));

        travelIdeaRepository.delete(travelIdea);
    }
    @Transactional(readOnly = true)
    public List<TravelIdeaDTO> getUserTravelIdeas(User user){

        return travelIdeaRepository.findByUser(user).stream()
                .map(this::mapToTravelIdeaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TravelIdeaDTO getCurrentTravelIdeaById(User user, Long travelIdeaId){
        TravelIdea travelIdea = travelIdeaRepository.findByIdAndUser(travelIdeaId, user)
                .orElseThrow(() -> new RuntimeException("TravelIdea not found"));
        return mapToTravelIdeaResponse(travelIdea);
    }

    @Transactional
    public TravelIdeaDTO addAdvertisementToTravelIdea(User user, Long advertisementId, Long travelIdeaId){

        TravelIdea travelIdea = travelIdeaRepository.findByIdAndUser(travelIdeaId, user)
                .orElseThrow(() -> new IllegalStateException("TravelIdea not found"));

        Advertisement advertisementToAdd = advertisementService.findById(advertisementId)
                .orElseThrow(() -> new IllegalStateException("Advertisement not found"));

        Set<Advertisement> travelIdeaAdvertisements = travelIdea.getAdvertisements();
        if(travelIdeaAdvertisements.contains(advertisementToAdd)){
            throw new IllegalStateException("Advertisement already added to this travel idea");
        }
        travelIdeaAdvertisements.add(advertisementToAdd);
        travelIdea.setAdvertisements(travelIdeaAdvertisements);
        travelIdeaRepository.save(travelIdea);

        return mapToTravelIdeaResponse(travelIdea);
    }

    @Transactional
    public TravelIdeaDTO removeAdvertisementFromTravelIdea(User user, Long travelIdeaId, Long advertisementId){
        TravelIdea travelIdea = travelIdeaRepository.findByIdAndUser(travelIdeaId, user)
                .orElseThrow(() -> new RuntimeException("TravelIdea not found"));

        Advertisement advertisementToRemove = advertisementService.findById(advertisementId)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));
        Set<Advertisement> travelIdeaAdvertisements = travelIdea.getAdvertisements();
        travelIdeaAdvertisements.remove(advertisementToRemove);
        travelIdea.setAdvertisements(travelIdeaAdvertisements);

        travelIdeaRepository.save(travelIdea);
        return mapToTravelIdeaResponse(travelIdea);
    }

    private TravelIdeaDTO mapToTravelIdeaResponse(TravelIdea travelIdea){
        TravelIdeaDTO response = new TravelIdeaDTO();
        response.setId(travelIdea.getId());
        response.setTitle(travelIdea.getTitle());
        response.setDescription(travelIdea.getDescription());
        response.setAdvertisementCount(travelIdea.getAdvertisements().size());
        List<CatalogAdvertisementResponse> advertisementFromIdea = travelIdea.getAdvertisements()
                .stream()
                .map(advertisementService::mapToCatalogResponse)
                .collect(Collectors.toList());
        response.setSavedAdvertisements(advertisementFromIdea);
        return response;
    }
}
