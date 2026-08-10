package com.cinema.depo.endpoint.rest.controller.reservation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cinema.depo.domain.movie.Genre;
import com.cinema.depo.domain.movie.Movie;
import com.cinema.depo.domain.movie.MovieRepository;
import com.cinema.depo.domain.projection.Projection;
import com.cinema.depo.domain.projection.ProjectionRepository;
import com.cinema.depo.domain.reservation.Reservation;
import com.cinema.depo.domain.reservation.ReservationRepository;
import com.cinema.depo.domain.room.Room;
import com.cinema.depo.domain.room.RoomRepository;
import com.cinema.depo.domain.room.Seat;
import com.cinema.depo.domain.room.SeatRepository;
import com.cinema.depo.domain.user.User;
import com.cinema.depo.domain.user.UserRepository;
import com.cinema.depo.domain.user.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.math.BigDecimal;
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
class ReservationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ProjectionRepository projectionRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private User createUser(String email, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("motdepasse123"));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String login(String email) throws Exception {
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

    // Construit une réservation complète (Room, Seat, Movie, Projection) pour un user donné
    private Reservation createReservationFor(User owner) {
        Room room = new Room();
        room.setNumber("A1");
        room.setCapacity(50);
        room = roomRepository.save(room);

        Seat seat = new Seat();
        seat.setNumber("A1-12");
        seat.setRoom(room);
        seat = seatRepository.save(seat);

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre(Genre.SCI_FI);
        movie.setDescription("test");
        movie.setDuration(Duration.ofMinutes(148));
        movie = movieRepository.save(movie);

        Projection projection = new Projection();
        projection.setRoom(room);
        projection.setMovie(movie);
        projection.setDatetime(Instant.now());
        projection.setSeatPrice(BigDecimal.valueOf(10));
        projection = projectionRepository.save(projection);

        Reservation reservation = new Reservation();
        reservation.setCreatedAt(Instant.now());
        reservation.setProjection(projection);
        reservation.setSeat(seat);
        reservation.setUser(owner);
        return reservationRepository.save(reservation);
    }

    @Test
    void clientShouldNotSeeReservationsList() throws Exception {
        User client = createUser("client-list-test@cinema.com", UserRole.CLIENT);
        String token = login(client.getEmail());

        mockMvc.perform(get("/reservations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerShouldSeeReservationsList() throws Exception {
        User manager = createUser("manager-list-test@cinema.com", UserRole.MANAGER);
        String token = login(manager.getEmail());

        mockMvc.perform(get("/reservations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void clientShouldSeeOwnReservation() throws Exception {
        User owner = createUser("owner-test@cinema.com", UserRole.CLIENT);
        Reservation reservation = createReservationFor(owner);
        String token = login(owner.getEmail());

        mockMvc.perform(get("/reservations/" + reservation.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void clientShouldNotSeeSomeoneElsesReservation() throws Exception {
        User owner = createUser("owner2-test@cinema.com", UserRole.CLIENT);
        Reservation reservation = createReservationFor(owner);

        User stranger = createUser("stranger-test@cinema.com", UserRole.CLIENT);
        String strangerToken = login(stranger.getEmail());

        mockMvc.perform(get("/reservations/" + reservation.getId())
                        .header("Authorization", "Bearer " + strangerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void employeeShouldSeeAnyReservation() throws Exception {
        User owner = createUser("owner3-test@cinema.com", UserRole.CLIENT);
        Reservation reservation = createReservationFor(owner);

        User employee = createUser("employee-test@cinema.com", UserRole.EMPLOYEE);
        String employeeToken = login(employee.getEmail());

        mockMvc.perform(get("/reservations/" + reservation.getId())
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }
}