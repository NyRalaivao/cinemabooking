package com.cinema.depo.domain.room;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
@NoArgsConstructor
public class Seat {

  @Id @GeneratedValue private UUID id;

  private String number;

  @ManyToOne
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;
}
