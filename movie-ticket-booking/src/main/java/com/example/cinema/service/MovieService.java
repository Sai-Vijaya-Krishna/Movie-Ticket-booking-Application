package com.example.cinema.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.cinema.entity.Movie;
import com.example.cinema.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository repo;

    public MovieService(MovieRepository repo) {
        this.repo = repo;
    }

    public List<Movie> getAllMovies() {
        return repo.findAll();
    }

    public Movie getMovieById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Movie saveMovie(Movie movie) {
        return repo.save(movie);
    }
    public void deleteMovie(Long id) {
        repo.deleteById(id);
    }
}
