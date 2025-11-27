package org.trackitall.trackitall.supply.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.repository.SupplierRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WithMockUser(username = "test", roles = {"ADMIN"})
@SpringBootTest
@AutoConfigureMockMvc
class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void cleanDb() {
        supplierRepository.deleteAll();
    }
    @Test
    void createSupplier_shouldReturn200() throws Exception {
        SupplierRequestDTO dto = SupplierRequestDTO.builder()
                .name("Supplier Ajkj")
                .contact("contact@tehhst.com")
                .rating(4.0)
                .leadTime(3)
                .rawMaterialId(List.of(1))
                .build();

        mockMvc.perform(post("/api/supply/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Supplier Ajkj"));
    }

    @Test
    void getAllSuppliers_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/supply/suppliers"))
                .andExpect(status().isOk());
    }

    @Test
    void searchSuppliers_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/supply/suppliers/search")
                        .param("name", "a"))
                .andExpect(status().isOk());
    }

    @Test
    void getSupplierById_notFound() throws Exception {
        mockMvc.perform(get("/api/supply/suppliers/999"))
                .andExpect(status().is4xxClientError());
    }
}
