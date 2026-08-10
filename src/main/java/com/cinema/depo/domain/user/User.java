package com.cinema.depo.domain.user;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id @GeneratedValue private UUID id;

  private String firstName;
  private String lastName;
  private LocalDate birthdate;

  @Column(unique = true)
  private String email;

  private String password;
  private String phone;

  @Enumerated(EnumType.STRING)
  private UserRole role;
}
