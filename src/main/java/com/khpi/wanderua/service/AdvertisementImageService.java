package com.khpi.wanderua.service;

import com.khpi.wanderua.entity.Advertisement;
import com.khpi.wanderua.entity.AdvertisementImage;
import com.khpi.wanderua.repository.AdvertisementImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class AdvertisementImageService {
    private final AdvertisementImageRepository advertisementImageRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    public AdvertisementImageService(AdvertisementImageRepository advertisementImageRepository) {
        this.advertisementImageRepository = advertisementImageRepository;
    }

    public void saveAdvertisementImages(Advertisement advertisement, List<MultipartFile> images) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);

                if (!image.isEmpty()) {
                    saveImage(advertisement, image, i);
                }
            }
        } catch (Exception e) {
            log.error("Error saving advertisement images: ", e);
            throw new RuntimeException("Помилка збереження зображень");
        }
    }
        private void saveImage(Advertisement advertisement, MultipartFile file, int order) throws IOException {
            /*String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";*/
            String filename = "adv_" + advertisement.getId() + "_" + order + "_" +
                    System.currentTimeMillis();

            Path filePath = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), filePath);

            AdvertisementImage imageEntity = AdvertisementImage.builder()
                    .advertisement(advertisement)
                    .name(filename)
                    .build();

            advertisementImageRepository.save(imageEntity);
        }
}