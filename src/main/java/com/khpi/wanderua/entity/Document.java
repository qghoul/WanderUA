package com.khpi.wanderua.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name="documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verify_request_id")
    @JsonIgnore
    private VerifyRequest verifyRequest;
    @Column(name = "file_name", nullable = false, length = 512)
    private String fileName;
    @Column(name = "file_type", nullable = false, length = 512)
    private String fileType;
    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

}
