package com.example.cinema.controller;

import com.example.cinema.entity.Movie;
import com.example.cinema.repository.MovieRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies")
@CrossOrigin(origins = "*")
public class AdminMovieController {

    private final MovieRepository movieRepository;

    public AdminMovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable Long id) {
        return movieRepository.findById(id).orElseThrow();
    }
    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }


    @PutMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        Movie existing = movieRepository.findById(id).orElseThrow();

        existing.setTitle(movie.getTitle());
        existing.setGenre(movie.getGenre());
        existing.setDuration(movie.getDuration());
        existing.setPrice(movie.getPrice());
        existing.setCode(movie.getCode());

        return movieRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }
}