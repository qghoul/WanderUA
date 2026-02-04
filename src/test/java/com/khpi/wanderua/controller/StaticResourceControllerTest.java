package com.khpi.wanderua.controller;
import com.khpi.wanderua.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaticResourceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StaticResourceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @TempDir
    Path tempDir; // JUnit сам создаст и удалит эту папку

    @Test
    void testGetReviewImage_Success() throws Exception {
        String filename = "test.jpg";
        String category = "reviews";

        Path categoryDir = tempDir.resolve(category);
        Files.createDirectories(categoryDir);
        Path realFile = categoryDir.resolve(filename);
        Files.write(realFile, "fake image content".getBytes());

        when(imageService.imageExists(filename, category)).thenReturn(true);
        when(imageService.getImagePath(filename, category)).thenReturn(realFile);

        mockMvc.perform(get("/images/reviews/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    void testGetReviewImage_InvalidFilename() throws Exception {
        String invalidFilename = "hacker%2Fattack.jpg";

        mockMvc.perform(get("/images/reviews/" + invalidFilename))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetReviewImage_NotFound() throws Exception {
        String filename = "missing.png";
        String category = "reviews";

        when(imageService.imageExists(filename, category)).thenReturn(false);

        mockMvc.perform(get("/images/reviews/{filename}", filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetReviewImage_ResourceNotReadable() throws Exception {
        String filename = "unreadable.gif";
        String category = "reviews";

        when(imageService.imageExists(filename, category)).thenReturn(true);
        Path imagePath = Paths.get("uploads", category, filename);
        when(imageService.getImagePath(filename, category)).thenReturn(imagePath);
    }
}
