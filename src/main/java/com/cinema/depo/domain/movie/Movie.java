// Movie.java
package com.cinema.depo.domain.movie;

import jakarta.persistence.*;
import java.time.Duration;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movie")
@Getter // génère tous les getters (getTitle(), getGenre()...)
@Setter // génère tous les setters
@NoArgsConstructor // JPA a besoin d'un constructeur vide pour instancier l'entité depuis la BDD
public class Movie {

  @Id @GeneratedValue // Postgres génère l'UUID automatiquement
  private UUID id;

  private String title;

  @Enumerated(
      EnumType.STRING) // stocke "COMEDY" en texte plutôt que 0,1,2... (plus lisible en base)
  private Genre genre;

  @Column(length = 2000) // sinon Postgres limite les VARCHAR par défaut
  private String description;

  private Duration duration;
}
