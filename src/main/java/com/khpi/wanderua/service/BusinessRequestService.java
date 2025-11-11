package com.khpi.wanderua.service;

import com.khpi.wanderua.dto.BusinessRepresentVerifyRequest;
import com.khpi.wanderua.dto.BusinessRequestDTO;
import com.khpi.wanderua.dto.SustainabilityStatusRequest;
import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.enums.VerifyRequestType;
import com.khpi.wanderua.repository.BusinessRepository;
import com.khpi.wanderua.repository.RoleRepository;
import com.khpi.wanderua.repository.UserRepository;
import com.khpi.wanderua.repository.VerifyRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessRequestService {
    private final BusinessRepository businessRepository;
    private final VerifyRequestRepository verifyRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DocumentService documentService;

    @Transactional
    public BusinessRequestDTO createBusinessRepresentVerifyRequest(
            Long userId,
            BusinessRepresentVerifyRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));

        //User and Business mapped One-to-One, check if user already is business represent or have business connected with unresolved VerifyRequest
        Role businessRole = roleRepository.findByName(RoleConstants.ROLE_BUSINESS).get();
        if (user.getRoles().contains(businessRole)) {
            throw new IllegalStateException("Ви вже є представником бізнесу, для додавання ще одного бізнесу сторіть новий акаунт");
        } else if (businessRepository.findByUser(user).isPresent()) {
            throw new IllegalStateException("Ваш попередній запит в обробці, тому ви не можете створити новий");
        }

        Business business;
        Optional<Business> optionalBusiness = businessRepository.findByName(request.getBusinessName());
        if (optionalBusiness.isPresent()) {
            business = optionalBusiness.get();
            if (verifyRequestRepository.existsVerifyRequestByBusinessAndConfirmedAndType(
                    business, true, VerifyRequestType.BUSINESS_VERIFY)) {
                throw new IllegalStateException("Бізнес з такою назвою вже зареєстровано");
            }

            business.setRepresentFullName(request.getRepresentFullName());
            business.setDescription(request.getBusinessDescription());
            business.setUser(user);
            businessRepository.save(business);

        } else {
            business = new Business();
            business.setUser(user);
            business.setName(request.getBusinessName());
            business.setRepresentFullName(request.getRepresentFullName());
            business.setDescription(request.getBusinessDescription());
            businessRepository.save(business);
        }

        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setBusiness(business);
        verifyRequest.setType(request.getVerifyRequestType());
        verifyRequest.setComment(request.getComment());
        verifyRequest.setUser(user);
        // Save verify request first WITHOUT documents
        VerifyRequest saved = verifyRequestRepository.save(verifyRequest);

        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            documentService.linkDocumentsToVerifyRequest(request.getDocumentIds(), saved);
            // Reload with documents
            saved = verifyRequestRepository.findByIdWithDocuments(saved.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));
        }

        log.info("Business verify request created with id: {}", saved.getId());

        return mapToVerifyRequestDTO(saved);
    }

    @Transactional
    public BusinessRequestDTO createSustainabilityStatusRequest(
            Long userId,
            SustainabilityStatusRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));

        Business business = businessRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Бізнес прив'язаний до акаунту не знайдено"));

        if (business.getSustainable_verify().equals(true)) {
            throw new IllegalStateException("Бізнесу вже надано статус сталого");
        }

        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setBusiness(business);
        verifyRequest.setUser(user);
        verifyRequest.setType(request.getVerifyRequestType());
        verifyRequest.setComment(request.getComment());
        // Save verify request first WITHOUT documents
        VerifyRequest saved = verifyRequestRepository.save(verifyRequest);

        //Link documents using IDs
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            documentService.linkDocumentsToVerifyRequest(request.getDocumentIds(), saved);
            // Reload with documents
            saved = verifyRequestRepository.findByIdWithDocuments(saved.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));
        }

        log.info("Sustainability status request created with id: {}", saved.getId());

        return mapToVerifyRequestDTO(saved);
    }

    @Transactional
    public BusinessRequestDTO resolveBusinessRepresentVerifyRequest(
            Long verifyRequestId,
            boolean confirmed,
            String adminComment) {
        log.info("Resolving business represent verify request {} with confirmation: {}", verifyRequestId, confirmed);

        VerifyRequest verifyRequest = verifyRequestRepository.findByIdWithDocuments(verifyRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));

        verifyRequest.setResolved(true);
        verifyRequest.setResolvedAt(LocalDateTime.now());
        verifyRequest.setConfirmed(confirmed);
        verifyRequest.setAdminComment(adminComment);
        User user = verifyRequest.getUser();

        if (confirmed) {
            user.setBusinessRepresentVerify(true);
            Set<Role> userRoles = user.getRoles();
            Role businessRole = roleRepository.findByName(RoleConstants.ROLE_BUSINESS).get();
            userRoles.add(businessRole);
            userRepository.save(user);
            log.info("Business represent verify request {} confirmed", verifyRequestId);
        } else {
            Business businessFromUserRequest = verifyRequest.getBusiness();
            verifyRequest.setBusiness(null);
            verifyRequestRepository.save(verifyRequest);

            documentService.deleteDocumentsForVerifyRequest(verifyRequestId);

            businessRepository.delete(businessFromUserRequest);
        }

        VerifyRequest saved = verifyRequestRepository.save(verifyRequest);
        return mapToVerifyRequestDTO(saved);
    }

    @Transactional
    public BusinessRequestDTO resolveSustainabilityStatusRequest(
            Long verifyRequestId,
            boolean confirmed,
            String adminComment) {
        log.info("Resolving sustainability status request {} with confirmation: {}", verifyRequestId, confirmed);

        VerifyRequest verifyRequest = verifyRequestRepository.findByIdWithDocuments(verifyRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));

        verifyRequest.setResolved(true);
        verifyRequest.setResolvedAt(LocalDateTime.now());
        verifyRequest.setConfirmed(confirmed);
        verifyRequest.setAdminComment(adminComment);

        if (confirmed) {
            Business businessFromUserRequest = verifyRequest.getBusiness();
            businessFromUserRequest.setSustainable_verify(true);
            businessRepository.save(businessFromUserRequest);
            log.info("Business {} marked as sustainable", businessFromUserRequest.getName());
        }

        VerifyRequest saved = verifyRequestRepository.save(verifyRequest);
        return mapToVerifyRequestDTO(saved);
    }


    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getAllBusinessRepresentVerifyRequests() {
        log.info("Fetching all Business Represent Verify requests");

        List<VerifyRequest> verifyRequests = verifyRequestRepository
                .findByTypeWithDocuments(VerifyRequestType.BUSINESS_VERIFY);

        log.info("Found {} business represent verify requests", verifyRequests.size());

        return verifyRequests.stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getAllSustainabilityStatusRequests() {
        log.info("Fetching all sustainability status requests");

        List<VerifyRequest> verifyRequests = verifyRequestRepository
                .findByTypeWithDocuments(VerifyRequestType.SUSTAINABLE_VERIFY);

        log.info("Found {} sustainability status requests", verifyRequests.size());

        return verifyRequests.stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getUnresolvedBusinessRepresentVerifyRequests() {
        log.info("Fetching unresolved Business Represent Verify requests");
        return verifyRequestRepository.findByResolvedAndTypeWithDocuments(false, VerifyRequestType.BUSINESS_VERIFY).stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getUnresolvedSustainabilityStatusRequests() {
        log.info("Fetching unresolved sustainability status requests");
        return verifyRequestRepository.findByResolvedAndTypeWithDocuments(false, VerifyRequestType.SUSTAINABLE_VERIFY).stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getResolvedBusinessRepresentVerifyRequests() {
        log.info("Fetching resolved Business Represent Verify requests");
        return verifyRequestRepository.findByResolvedAndTypeWithDocuments(true, VerifyRequestType.BUSINESS_VERIFY).stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getResolvedSustainabilityStatusRequests() {
        log.info("Fetching resolved sustainability status requests");
        return verifyRequestRepository.findByResolvedAndTypeWithDocuments(true, VerifyRequestType.SUSTAINABLE_VERIFY).stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getBusinessRepresentVerifyRequestsByStatus(String status) {
        List<VerifyRequest> verifyRequests;

        switch (status.toLowerCase()) {
            case "unresolved":
                verifyRequests = verifyRequestRepository.findByResolvedAndTypeWithDocuments(false, VerifyRequestType.BUSINESS_VERIFY);
                break;
            case "resolved":
                verifyRequests = verifyRequestRepository.findByResolvedAndTypeWithDocuments(true, VerifyRequestType.BUSINESS_VERIFY);
                break;
            case "confirmed":
                verifyRequests = verifyRequestRepository.findByConfirmedAndTypeWithDocuments(true, VerifyRequestType.BUSINESS_VERIFY);
                break;
            case "rejected":
                verifyRequests = verifyRequestRepository.findByConfirmedAndTypeWithDocuments(false, VerifyRequestType.BUSINESS_VERIFY);
                break;
            default:
                verifyRequests = verifyRequestRepository.findByTypeWithDocuments(VerifyRequestType.BUSINESS_VERIFY);
        }

        log.info("Found {} requests with status: {}", verifyRequests.size(), status);

        return verifyRequests.stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getSustainabilityStatusRequestsByStatus(String status) {
        List<VerifyRequest> verifyRequests;

        switch (status.toLowerCase()) {
            case "unresolved":
                verifyRequests = verifyRequestRepository.findByResolvedAndTypeWithDocuments(false, VerifyRequestType.SUSTAINABLE_VERIFY);
                break;
            case "resolved":
                verifyRequests = verifyRequestRepository.findByResolvedAndTypeWithDocuments(true, VerifyRequestType.SUSTAINABLE_VERIFY);
                break;
            case "confirmed":
                verifyRequests = verifyRequestRepository.findByConfirmedAndTypeWithDocuments(true, VerifyRequestType.SUSTAINABLE_VERIFY);
                break;
            case "rejected":
                verifyRequests = verifyRequestRepository.findByConfirmedAndTypeWithDocuments(false, VerifyRequestType.SUSTAINABLE_VERIFY);
                break;
            default:
                verifyRequests = verifyRequestRepository.findByTypeWithDocuments(VerifyRequestType.SUSTAINABLE_VERIFY);
        }

        log.info("Found {} requests with status: {}", verifyRequests.size(), status);

        return verifyRequests.stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessRequestDTO> getUserBusinessRequests(User user) {
        log.info("Fetching all business requests for user {}", user.getId());

        List<VerifyRequest> requests = verifyRequestRepository.findByUserWithDocuments(user);

        log.info("Found {} requests for user {}", requests.size(), user.getId());

        return requests.stream()
                .map(this::mapToVerifyRequestDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Business> getUserBusiness(User user) {
        return businessRepository.findByUser(user);
    }

    private BusinessRequestDTO mapToVerifyRequestDTO(VerifyRequest verifyRequest) {
        log.debug("Mapping VerifyRequest {} to DTO", verifyRequest.getId());

        BusinessRequestDTO dto = new BusinessRequestDTO();
        dto.setId(verifyRequest.getId());

        // User info
        if (verifyRequest.getUser() != null) {
            dto.setUserId(verifyRequest.getUser().getId());
            dto.setUsername(verifyRequest.getUser().getUsername());
        }

        // Business info
        if (verifyRequest.getBusiness() != null) {
            dto.setBusinessId(verifyRequest.getBusiness().getId());
            dto.setBusinessName(verifyRequest.getBusiness().getName());
            dto.setBusinessDescription(verifyRequest.getBusiness().getDescription());
            dto.setRepresentFullName(verifyRequest.getBusiness().getRepresentFullName());
        }

        // Request info
        dto.setVerifyRequestType(verifyRequest.getType());
        dto.setComment(verifyRequest.getComment());
        dto.setResolved(verifyRequest.isResolved());
        dto.setConfirmed(verifyRequest.getConfirmed());
        dto.setAdminComment(verifyRequest.getAdminComment());
        dto.setCreatedAt(verifyRequest.getCreatedAt());
        dto.setResolvedAt(verifyRequest.getResolvedAt());

        List<BusinessRequestDTO.DocumentDTO> documentDTOs = new ArrayList<>();

        if (verifyRequest.getDocuments() != null && !verifyRequest.getDocuments().isEmpty()) {
            log.debug("Mapping {} documents for verify request {}",
                    verifyRequest.getDocuments().size(), verifyRequest.getId());

            for (Document doc : verifyRequest.getDocuments()) {
                BusinessRequestDTO.DocumentDTO docDTO = BusinessRequestDTO.DocumentDTO.builder()
                        .id(doc.getId())
                        .fileName(doc.getFileName())
                        .fileType(doc.getFileType())
                        .filePath(doc.getFilePath())
                        .build();
                documentDTOs.add(docDTO);
            }
        } else {
            log.debug("No documents found for verify request {}", verifyRequest.getId());
        }

        dto.setDocumentSet(documentDTOs);
        log.debug("Total documents mapped: {}", documentDTOs.size());

        return dto;
    }

}
