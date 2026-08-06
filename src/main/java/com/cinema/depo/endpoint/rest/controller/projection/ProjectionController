package com.cinema.depo.endpoint.rest.controller.projection;

import com.cinema.depo.domain.projection.Projection;
import com.cinema.depo.domain.projection.ProjectionRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/projections")
public class ProjectionController {
  private final ProjectionRepository projectionRepository;

  @GetMapping
  public ResponseEntity<List<Projection>> getProjections() {
    return ResponseEntity.ok(projectionRepository.findAll());
  }

  @PutMapping
  @PreAuthorize("hasRole('MANAGER')") // 403 pour CLIENT/EMPLOYEE, 200 pour MANAGER
  public ResponseEntity<Projection> createOrUpdateProjection(@RequestBody Projection projection) {
    return ResponseEntity.ok(projectionRepository.save(projection));
  }
}