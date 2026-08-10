package com.cinema.depo.endpoint.rest.controller.projection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cinema.depo.domain.movie.Genre;
import com.cinema.depo.domain.movie.Movie;
import com.cinema.depo.domain.movie.MovieRepository;
import com.cinema.depo.domain.room.Room;
import com.cinema.depo.domain.room.RoomRepository;
import com.cinema.depo.domain.user.User;
import com.cinema.depo.domain.user.UserRepository;
import com.cinema.depo.domain.user.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
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
class ProjectionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private MovieRepository movieRepository;
  @Autowired private RoomRepository roomRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private String createUserAndLogin(String email, UserRole role) throws Exception {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("motdepasse123"));
    user.setRole(role);
    userRepository.save(user);

    String loginBody =
        """
        {"email":"%s","password":"motdepasse123"}
        """
            .formatted(email);

    MvcResult result =
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode json = OBJECT_MAPPER.readTree(result.getResponse().getContentAsString());
    return json.get("token").asText();
  }

  @Test
  void getProjectionsShouldRequireAuthentication() throws Exception {
    mockMvc.perform(get("/projections")).andExpect(status().isForbidden());
  }

  @Test
  void clientShouldNotCreateProjection() throws Exception {
    Movie movie = movieRepository.save(newMovie());
    Room room = roomRepository.save(newRoom());
    String token = createUserAndLogin("client-projection-test@cinema.com", UserRole.CLIENT);

    mockMvc
        .perform(
            put("/projections")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
{"datetime":"2026-12-01T20:00:00Z","seatPrice":10,"movie":{"id":"%s"},"room":{"id":"%s"}}
"""
                        .formatted(movie.getId(), room.getId())))
        .andExpect(status().isForbidden());
  }

  @Test
  void managerShouldCreateProjection() throws Exception {
    Movie movie = movieRepository.save(newMovie());
    Room room = roomRepository.save(newRoom());
    String token = createUserAndLogin("manager-projection-test@cinema.com", UserRole.MANAGER);

    mockMvc
        .perform(
            put("/projections")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
{"datetime":"2026-12-01T20:00:00Z","seatPrice":10,"movie":{"id":"%s"},"room":{"id":"%s"}}
"""
                        .formatted(movie.getId(), room.getId())))
        .andExpect(status().isOk());
  }

  private Movie newMovie() {
    Movie movie = new Movie();
    movie.setTitle("Inception");
    movie.setGenre(Genre.SCI_FI);
    movie.setDescription("test");
    movie.setDuration(Duration.ofMinutes(148));
    return movie;
  }

  private Room newRoom() {
    Room room = new Room();
    room.setNumber("A1");
    room.setCapacity(50);
    return room;
  }
}
