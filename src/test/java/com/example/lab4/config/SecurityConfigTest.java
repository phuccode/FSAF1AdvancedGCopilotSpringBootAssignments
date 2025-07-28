package com.example.lab4.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Dashboard Access Tests")
    class DashboardAccessTests {
        
        @Test
        @WithMockUser(username = "user@example.com", roles = "USER")
        @DisplayName("Regular user should not access dashboard")
        void userCannotAccessDashboard() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        @DisplayName("Admin should access dashboard")
        void adminCanAccessDashboard() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user should not access dashboard")
        void anonymousCannotAccessDashboard() throws Exception {
            mockMvc.perform(get("/api/dashboard/stats")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Review Access Tests")
    class ReviewAccessTests {
        
        @Test
        @WithMockUser(username = "user@example.com", roles = "USER")
        @DisplayName("User can view reviews")
        void userCanAccessReviews() throws Exception {
            mockMvc.perform(get("/api/reviews")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "user@example.com", roles = "USER")
        @DisplayName("User can create review")
        void userCanCreateReview() throws Exception {
            mockMvc.perform(post("/api/reviews/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"productId\": 1, \"rating\": 5, \"comment\": \"Great product!\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous cannot create review")
        void anonymousCannotCreateReview() throws Exception {
            mockMvc.perform(post("/api/reviews/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"productId\": 1, \"rating\": 5, \"comment\": \"Great product!\"}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Product Management Tests")
    class ProductManagementTests {
        
        @Test
        @WithMockUser(username = "user@example.com", roles = "USER")
        @DisplayName("User cannot create product")
        void userCannotCreateProduct() throws Exception {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Test Product\", \"price\": 99.99}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        @DisplayName("Admin can create product")
        void adminCanCreateProduct() throws Exception {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Test Product\", \"price\": 99.99}"))
                    .andExpect(status().isOk());
        }
    }
}
