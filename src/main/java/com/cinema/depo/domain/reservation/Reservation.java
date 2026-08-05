package com.cinema.depo.domain.reservation;

import com.cinema.depo.domain.projection.Projection;
import com.cinema.depo.domain.room.Seat;
import com.cinema.depo.domain.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id @GeneratedValue
    private UUID id;

    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "projection_id", nullable = false)
    private Projection projection;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}