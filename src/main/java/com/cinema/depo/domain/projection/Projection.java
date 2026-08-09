package com.cinema.depo.domain.projection;

import com.cinema.depo.domain.movie.Movie;
import com.cinema.depo.domain.room.Room;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projection")
@Getter
@Setter
@NoArgsConstructor
public class Projection {

  @Id @GeneratedValue private UUID id;

  private Instant datetime;
  private BigDecimal seatPrice;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @ManyToOne
  @JoinColumn(name = "movie_id", nullable = false)
  private Movie movie;
}
