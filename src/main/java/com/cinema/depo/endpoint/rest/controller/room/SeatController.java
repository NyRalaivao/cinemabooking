package com.cinema.depo.endpoint.rest.controller.room;

import com.cinema.depo.domain.room.Seat;
import com.cinema.depo.domain.room.SeatRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/seats")
public class SeatController {

  private final SeatRepository seatRepository;

  @GetMapping
  public ResponseEntity<List<Seat>> getSeatsByRoom(@RequestParam UUID roomId) {
    return ResponseEntity.ok(seatRepository.findByRoomId(roomId));
  }

  @PutMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<Seat> createOrUpdateSeat(@RequestBody Seat seat) {
    return ResponseEntity.ok(seatRepository.save(seat));
  }
}
