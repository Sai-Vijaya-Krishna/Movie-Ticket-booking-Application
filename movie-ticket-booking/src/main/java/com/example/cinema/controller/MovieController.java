package com.example.cinema.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.example.cinema.entity.Movie;
import com.example.cinema.service.MovieService;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return service.getAllMovies();
    }

    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        return service.saveMovie(movie);
    }
 // DELETE MOVIE
    @DeleteMapping("/{id}")
    public String deleteMovie(@PathVariable Long id) {
        service.deleteMovie(id);
        return "Movie deleted successfully";
    }
}