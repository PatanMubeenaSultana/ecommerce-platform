package com.ecommerce.user_service;

import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerUserWithValidDetails() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("testuser101@example.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void registerUserWithMissingFirstName() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("");
        request.setLastName("user");
        request.setEmail("testuser101@example.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUserWithInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("invalid-email");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void registerUserWithShortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("testuser103@example.com");
        request.setPassword("abc123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void registerUserWithDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("admin@gmail.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void registeredUserPasswordShouldBeEncrypted() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Hash");
        request.setLastName("Test");
        request.setEmail("hashtest104@gmail.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("hashtest104@gmail.com")
                .orElseThrow();

        assertNotEquals("Password123", user.getPassword());
        assertTrue(passwordEncoder.matches(
                "Password123", user.getPassword()
        ));
    }

    @Test
    public void loginWithValidCredentialsShouldReturnToken() throws Exception{
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        String token = json.get("token").asText();

        assertNotNull(token);
        assertFalse(token.isBlank());

    }

    @Test
    void loginWithIncorrectPasswordShouldBeRejected() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("WrongPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void loginWithUnregisteredEmailShouldBeRejected() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknownuser999@example.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


}
