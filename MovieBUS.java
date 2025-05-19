package com.movie.bus;

import com.movie.dao.MovieDAO;
import com.movie.model.Movie;

import java.sql.SQLException;
import java.util.List;

public class MovieBUS {
    private MovieDAO movieDAO = new MovieDAO();

    public void addMovie(Movie movie) throws SQLException {
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (movie.getAgeRestriction() < 0) {
            throw new IllegalArgumentException("Age restriction cannot be negative");
        }
        movieDAO.addMovie(movie);
    }

    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.getAllMovies();
    }

    public Movie getMovieById(int movieID) throws SQLException {
        if (movieID <= 0) {
            throw new IllegalArgumentException("Invalid movie ID");
        }
        return movieDAO.getMovieById(movieID);
    }

    public void updateMovie(Movie movie) throws SQLException {
        if (movie.getMovieID() <= 0) {
            throw new IllegalArgumentException("Invalid movie ID");
        }
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (movie.getAgeRestriction() < 0) {
            throw new IllegalArgumentException("Age restriction cannot be negative");
        }
        movieDAO.updateMovie(movie);
    }

    public void deleteMovie(int movieID) throws SQLException {
        if (movieID <= 0) {
            throw new IllegalArgumentException("Invalid movie ID");
        }
        movieDAO.deleteMovie(movieID);
    }

    public List<String> getAllGenres() throws SQLException {
        return movieDAO.getAllGenres();
    }

    public List<String> getAllCountries() throws SQLException {
        return movieDAO.getAllCountries();
    }
}