package com.khpi.wanderua.controller;

import com.khpi.wanderua.config.SecurityHelper;
import com.khpi.wanderua.dto.CatalogAdvertisementResponse;
import com.khpi.wanderua.dto.CatalogFilterRequest;
import com.khpi.wanderua.dto.CatalogResponse;
import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.service.AdvertisementService;
import com.khpi.wanderua.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdvertisementController.class,
        excludeAutoConfiguration = {
                RedisAutoConfiguration.class,
                RedisRepositoriesAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class AdvertisementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertisementService advertisementService;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityHelper securityHelper;

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

        when(advertisementService.getCatalogAdvertisements(any(CatalogFilterRequest.class)))
                .thenReturn(sampleResponse);

        // Act & Assert: Perform GET request and expect 200 OK and JSON content
        mockMvc.perform(get("/advertisements/catalog")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // Print request and response for debugging
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
                .andDo(print()) // Print request and response for debugging
                .andExpect(status().isOk())
                .andExpect(content().json("{\"advertisements\":[]}"));
    }
}
