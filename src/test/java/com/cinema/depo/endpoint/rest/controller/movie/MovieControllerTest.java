package com.cinema.depo.endpoint.rest.controller.movie;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Petite méthode utilitaire : inscrit un utilisateur, le connecte, et renvoie son token JWT
    private String registerAndLogin(String email, String password) throws Exception {
        String registerBody = """
        {"email":"%s","password":"%s","firstName":"Test","lastName":"User"}
        """.formatted(email, password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        String loginBody = """
        {"email":"%s","password":"%s"}
        """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = OBJECT_MAPPER.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    @Test
    void clientShouldNotBeAbleToCreateMovie() throws Exception {
        String token = registerAndLogin("client-movie-test@cinema.com", "motdepasse123");

        String movieBody = """
        {"title":"Inception","genre":"SCI_FI","description":"test","duration":"PT2H28M"}
        """;

        // Un CLIENT (rôle par défaut à l'inscription) doit recevoir 403
        mockMvc.perform(put("/movies")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerShouldBeAbleToCreateMovie() throws Exception {
        String email = "manager-movie-test@cinema.com";
        String token = registerAndLogin(email, "motdepasse123");

        // On promeut directement en base, comme on l'a fait via H2-console, mais ici automatisé
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setRole(UserRole.MANAGER);
        userRepository.save(user);

        // ⚠️ Il faut se reconnecter : le rôle est figé dans le token au moment du login
        String loginBody = """
        {"email":"%s","password":"motdepasse123"}
        """.formatted(email);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = OBJECT_MAPPER.readTree(loginResult.getResponse().getContentAsString());
        String managerToken = json.get("token").asText();

        String movieBody = """
        {"title":"Inception","genre":"SCI_FI","description":"test","duration":"PT2H28M"}
        """;

        mockMvc.perform(put("/movies")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieBody))
                .andExpect(status().isOk());
    }

    @Test
    void getMoviesShouldRequireAuthentication() throws Exception {
        // Aucun header Authorization → 403, comme testé à la main avec curl
        mockMvc.perform(get("/movies"))
                .andExpect(status().isForbidden());
    }
}