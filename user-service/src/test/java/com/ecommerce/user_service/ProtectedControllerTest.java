package com.ecommerce.user_service;

import com.ecommerce.user_service.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ProtectedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    public void customerCanAccessProfile() throws  Exception{
        mockMvc.perform(get("/api/users/profile")
                .with(user("customer@gmail.com")
                        .authorities(() -> "CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email")
                        .value("customer@gmail.com"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void unauthenticatedUserCannotAccessProfile() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidTokenCannotAccessProfile() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer invalid-token-123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanAccessProfile() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .with(user("admin@gmail.com")
                                .authorities(() -> "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("admin@gmail.com"));
    }

    @Test
    void validJwtCanAccessProfile() throws Exception {
        String email = "customer@gmail.com";
        String token = jwtService.generateToken(email);

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(email));
    }

    @Test
    void expiredJwtCannotAccessProfile() throws Exception {
        String email = "customer@gmail.com";

        String expiredToken = Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(
                        "mySecretKeymySecretKeymySecretKeymySecretKey"
                                .getBytes()))
                .compact();

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwtSignedWithWrongSecretCannotAccessProfile() throws Exception {
        String email = "customer@gmail.com";

        String wrongSecretToken = Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor(
                        "anotherSecretKeyanotherSecretKeyanotherSecretKey"
                                .getBytes()))
                .compact();

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + wrongSecretToken))
                .andExpect(status().isUnauthorized());
    }



}
