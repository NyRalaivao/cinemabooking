package com.cinema.depo.endpoint.rest.controller.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cinema.depo.domain.user.User;
import com.cinema.depo.domain.user.UserRepository;
import com.cinema.depo.domain.user.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String createUserAndLogin(String email, UserRole role) throws Exception {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("motdepasse123"));
        user.setRole(role);
        userRepository.save(user);

        String loginBody = """
        {"email":"%s","password":"motdepasse123"}
        """.formatted(email);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = OBJECT_MAPPER.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    @Test
    void getRoomsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/rooms"))
                .andExpect(status().isForbidden());
    }

    @Test
    void clientShouldNotCreateRoom() throws Exception {
        String token = createUserAndLogin("client-room-test@cinema.com", UserRole.CLIENT);

        mockMvc.perform(put("/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"number":"A1","capacity":50}
                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerShouldCreateRoom() throws Exception {
        String token = createUserAndLogin("manager-room-test@cinema.com", UserRole.MANAGER);

        mockMvc.perform(put("/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"number":"A1","capacity":50}
                """))
                .andExpect(status().isOk());
    }
}