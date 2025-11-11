package com.khpi.wanderua.repository;

import com.khpi.wanderua.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByVerifyRequestId(Long verifyRequestId);

    boolean existsByVerifyRequestId(Long verifyRequestId);

    long countByVerifyRequestId(Long verifyRequestId);

    void deleteByVerifyRequestId(Long verifyRequestId);
}