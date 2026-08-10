// MovieController.java
package com.cinema.depo.endpoint.rest.controller.movie;

import com.cinema.depo.domain.movie.Movie;
import com.cinema.depo.domain.movie.MovieRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/movies")
public class MovieController {
  private final MovieRepository movieRepository;

  @GetMapping
  public ResponseEntity<List<Movie>> getMovies() {
    return ResponseEntity.ok(movieRepository.findAll());
  }

  @PutMapping
  @PreAuthorize("hasRole('MANAGER')") // 403 pour CLIENT/EMPLOYEE, 200 pour MANAGER
  public ResponseEntity<Movie> createOrUpdateMovie(@RequestBody Movie movie) {
    return ResponseEntity.ok(movieRepository.save(movie));
  }
}
