package com.cinema.depo.endpoint.rest.controller.reservation;

import com.cinema.depo.domain.reservation.Reservation;
import com.cinema.depo.domain.reservation.ReservationRepository;
import com.cinema.depo.domain.user.User;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationRepository reservationRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Reservation>> getReservations() {
        return ResponseEntity.ok(reservationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        boolean isOwner = reservation.getUser().getId().equals(currentUser.getId());
        boolean isStaff = currentUser.getRole().name().equals("MANAGER")
                || currentUser.getRole().name().equals("EMPLOYEE");
        if (!isOwner && !isStaff) {
            return ResponseEntity.status(403).build(); // un CLIENT ne voit que SA réservation
        }
        return ResponseEntity.ok(reservation);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    public ResponseEntity<Reservation> createOrUpdateReservation(@RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationRepository.save(reservation));
    }
}