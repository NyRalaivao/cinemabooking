package com.cinema.depo.domain.movie;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void shouldSaveAndRetrieveMovie() {
        // Arrange : on prépare un film de test
        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre(Genre.SCI_FI);
        movie.setDescription("Un voleur qui s'infiltre dans les rêves");
        movie.setDuration(Duration.ofMinutes(148));

        Movie saved = movieRepository.save(movie);

        assertThat(saved.getId()).isNotNull();
        assertThat(movieRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Movie::getTitle)
                .isEqualTo("Inception");
    }
}