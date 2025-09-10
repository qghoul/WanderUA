package com.khpi.wanderua.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name="documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verify_request_id")
    private VerifyRequest verifyRequest;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;
    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

}
