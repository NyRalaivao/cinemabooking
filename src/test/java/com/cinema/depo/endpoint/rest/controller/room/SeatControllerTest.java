package com.cinema.depo.endpoint.rest.controller.room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cinema.depo.domain.room.Room;
import com.cinema.depo.domain.room.RoomRepository;
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
class SeatControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
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

    private Room createRoom() {
        Room room = new Room();
        room.setNumber("A1");
        room.setCapacity(50);
        return roomRepository.save(room);
    }

    @Test
    void getSeatsShouldRequireAuthentication() throws Exception {
        Room room = createRoom();
        mockMvc.perform(get("/seats").param("roomId", room.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void clientShouldNotCreateSeat() throws Exception {
        Room room = createRoom();
        String token = createUserAndLogin("client-seat-test@cinema.com", UserRole.CLIENT);

        mockMvc.perform(put("/seats")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"number":"A1-01","room":{"id":"%s"}}
                """.formatted(room.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerShouldCreateSeat() throws Exception {
        Room room = createRoom();
        String token = createUserAndLogin("manager-seat-test@cinema.com", UserRole.MANAGER);

        mockMvc.perform(put("/seats")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"number":"A1-01","room":{"id":"%s"}}
                """.formatted(room.getId())))
                .andExpect(status().isOk());
    }
}