package com.cinema.depo.endpoint.rest.controller.auth;

import com.cinema.depo.domain.user.User;
import com.cinema.depo.domain.user.UserRepository;
import com.cinema.depo.domain.user.UserRole;
import com.cinema.depo.endpoint.security.JwtService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail()).orElse(null);
    if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(new LoginResponse(jwtService.generateToken(user)));
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return ResponseEntity.status(409).build(); // 409 = conflit, email déjà pris
    }
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword())); // hashage, jamais en clair
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setRole(UserRole.CLIENT); // rôle par défaut à l'inscription
    userRepository.save(user);
    return ResponseEntity.ok().build();
  }

  @Getter
  @Setter
  public static class LoginRequest {
    private String email;
    private String password;
  }

  @Getter
  @AllArgsConstructor
  public static class LoginResponse {
    private String token;
  }

  @Getter
  @Setter
  public static class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
  }
}
