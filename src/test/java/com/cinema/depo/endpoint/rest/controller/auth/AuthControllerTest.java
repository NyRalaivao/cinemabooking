package com.cinema.depo.endpoint.rest.controller.auth;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldRegisterThenLoginAndReceiveToken() throws Exception {
    String registerBody =
        """
{"email":"test-junit@cinema.com","password":"motdepasse123","firstName":"Test","lastName":"Junit"}
""";

    // Étape 1 : inscription — on attend un 200
    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerBody))
        .andExpect(status().isOk());

    String loginBody =
        """
        {"email":"test-junit@cinema.com","password":"motdepasse123"}
        """;

    // Étape 2 : connexion — on attend un 200 avec un token non-vide dans la réponse
    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token", notNullValue()));
  }

  @Test
  void shouldRejectLoginWithWrongPassword() throws Exception {
    String registerBody =
        """
{"email":"test-wrongpass@cinema.com","password":"bonmotdepasse","firstName":"Test","lastName":"Junit"}
""";
    mockMvc
        .perform(
            post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerBody))
        .andExpect(status().isOk());

    String wrongLoginBody =
        """
        {"email":"test-wrongpass@cinema.com","password":"mauvais-mot-de-passe"}
        """;

    // On attend un 401, pas un 200
    mockMvc
        .perform(
            post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(wrongLoginBody))
        .andExpect(status().isUnauthorized());
  }
}
