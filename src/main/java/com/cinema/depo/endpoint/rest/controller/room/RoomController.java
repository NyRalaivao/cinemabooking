package com.cinema.depo.endpoint.rest.controller.room;

import com.cinema.depo.domain.room.Room;
import com.cinema.depo.domain.room.RoomRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

  private final RoomRepository roomRepository;

  @GetMapping
  public ResponseEntity<List<Room>> getRooms() {
    return ResponseEntity.ok(roomRepository.findAll());
  }

  @PutMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<Room> createOrUpdateRoom(@RequestBody Room room) {
    return ResponseEntity.ok(roomRepository.save(room));
  }
}
