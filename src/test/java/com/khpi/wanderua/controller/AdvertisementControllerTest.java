package com.khpi.wanderua.controller;

import com.khpi.wanderua.dto.CatalogAdvertisementResponse;
import com.khpi.wanderua.dto.CatalogFilterRequest;
import com.khpi.wanderua.dto.CatalogResponse;
import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.service.AdvertisementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdvertisementController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdvertisementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvertisementService advertisementService;

    @Test
    void testGetCatalogReturnsOkAndJson() throws Exception {
        // Arrange: Prepare mock CatalogResponse with minimal valid data
        CatalogAdvertisementResponse advert = CatalogAdvertisementResponse.builder()
                .id(1L)
                .title("Sample Advertisement")
                .advertisementType(AdvertisementType.RESTAURANT)
                .build();

        CatalogResponse sampleResponse = CatalogResponse.builder()
                .advertisements(List.of(advert))
                .selectedCity("Kyiv")
                .maxPriceInCatalog(BigDecimal.valueOf(1000))
                .totalElements(1)
                .totalPages(1)
                .currentPage(0)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        // Mock the service method with any CatalogFilterRequest argument
        when(advertisementService.getCatalogAdvertisements(any(CatalogFilterRequest.class)))
                .thenReturn(sampleResponse);

        // Act & Assert: Perform GET request and expect 200 OK and JSON content
        mockMvc.perform(get("/advertisements/catalog")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCatalogReturnsEmptyList() throws Exception {
        // Arrange: Mock service to return empty CatalogResponse
        CatalogResponse emptyResponse = CatalogResponse.builder()
                .advertisements(List.of())
                .selectedCity(null)
                .maxPriceInCatalog(BigDecimal.ZERO)
                .totalElements(0)
                .totalPages(0)
                .currentPage(0)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(advertisementService.getCatalogAdvertisements(any(CatalogFilterRequest.class)))
                .thenReturn(emptyResponse);

        // Act & Assert: Perform GET request and expect 200 OK and JSON content with empty advertisements list
        mockMvc.perform(get("/advertisements/catalog")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"advertisements\":[]}"));
    }
}
