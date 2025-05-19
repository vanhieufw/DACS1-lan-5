package com.movie.dao;

import com.movie.model.Movie;
import com.movie.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    public void addMovie(Movie movie) throws SQLException {
        String query = "INSERT INTO Movie (Title, Description, Duration, Director, GenreID, Poster, StartDate, EndDate, ProductionYear, CountryID, AgeRestriction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getDirector());
            stmt.setInt(5, movie.getGenreID());
            stmt.setString(6, movie.getPoster());
            stmt.setDate(7, movie.getStartDate());
            stmt.setDate(8, movie.getEndDate());
            stmt.setInt(9, movie.getProductionYear());
            stmt.setInt(10, movie.getCountryID());
            stmt.setInt(11, movie.getAgeRestriction());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    movie.setMovieID(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to add movie: " + e.getMessage(), e);
        }
    }

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT m.*, g.GenreName, c.CountryName FROM Movie m " +
                "LEFT JOIN Genre g ON m.GenreID = g.GenreID " +
                "LEFT JOIN Country c ON m.CountryID = c.CountryID";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setMovieID(rs.getInt("MovieID"));
                movie.setTitle(rs.getString("Title"));
                movie.setDescription(rs.getString("Description"));
                movie.setDuration(rs.getInt("Duration"));
                movie.setDirector(rs.getString("Director"));
                movie.setGenreID(rs.getInt("GenreID"));
                movie.setGenreName(rs.getString("GenreName"));
                movie.setPoster(rs.getString("Poster"));
                movie.setStartDate(rs.getDate("StartDate"));
                movie.setEndDate(rs.getDate("EndDate"));
                movie.setProductionYear(rs.getInt("ProductionYear"));
                movie.setCountryID(rs.getInt("CountryID"));
                movie.setCountryName(rs.getString("CountryName"));
                movie.setAgeRestriction(rs.getInt("AgeRestriction"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve movies: " + e.getMessage(), e);
        }
        return movies;
    }

    public Movie getMovieById(int movieID) throws SQLException {
        String query = "SELECT m.*, g.GenreName, c.CountryName FROM Movie m " +
                "LEFT JOIN Genre g ON m.GenreID = g.GenreID " +
                "LEFT JOIN Country c ON m.CountryID = c.CountryID " +
                "WHERE m.MovieID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Movie movie = new Movie();
                    movie.setMovieID(rs.getInt("MovieID"));
                    movie.setTitle(rs.getString("Title"));
                    movie.setDescription(rs.getString("Description"));
                    movie.setDuration(rs.getInt("Duration"));
                    movie.setDirector(rs.getString("Director"));
                    movie.setGenreID(rs.getInt("GenreID"));
                    movie.setGenreName(rs.getString("GenreName"));
                    movie.setPoster(rs.getString("Poster"));
                    movie.setStartDate(rs.getDate("StartDate"));
                    movie.setEndDate(rs.getDate("EndDate"));
                    movie.setProductionYear(rs.getInt("ProductionYear"));
                    movie.setCountryID(rs.getInt("CountryID"));
                    movie.setCountryName(rs.getString("CountryName"));
                    movie.setAgeRestriction(rs.getInt("AgeRestriction"));
                    return movie;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve movie with ID " + movieID + ": " + e.getMessage(), e);
        }
        return null;
    }

    public void updateMovie(Movie movie) throws SQLException {
        String query = "UPDATE Movie SET Title = ?, Description = ?, Duration = ?, Director = ?, GenreID = ?, Poster = ?, StartDate = ?, EndDate = ?, ProductionYear = ?, CountryID = ?, AgeRestriction = ? WHERE MovieID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getDirector());
            stmt.setInt(5, movie.getGenreID());
            stmt.setString(6, movie.getPoster());
            stmt.setDate(7, movie.getStartDate());
            stmt.setDate(8, movie.getEndDate());
            stmt.setInt(9, movie.getProductionYear());
            stmt.setInt(10, movie.getCountryID());
            stmt.setInt(11, movie.getAgeRestriction());
            stmt.setInt(12, movie.getMovieID());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No movie found with ID " + movie.getMovieID());
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to update movie: " + e.getMessage(), e);
        }
    }

    public void deleteMovie(int movieID) throws SQLException {
        String query = "DELETE FROM Movie WHERE MovieID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No movie found with ID " + movieID);
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to delete movie: " + e.getMessage(), e);
        }
    }

    public List<String> getAllGenres() throws SQLException {
        List<String> genres = new ArrayList<>();
        String query = "SELECT GenreName FROM Genre";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                genres.add(rs.getString("GenreName"));
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve genres: " + e.getMessage(), e);
        }
        return genres;
    }

    public List<String> getAllCountries() throws SQLException {
        List<String> countries = new ArrayList<>();
        String query = "SELECT CountryName FROM Country";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                countries.add(rs.getString("CountryName"));
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve countries: " + e.getMessage(), e);
        }
        return countries;
    }

    public int getGenreIdByName(String genreName) throws SQLException {
        String query = "SELECT GenreID FROM Genre WHERE GenreName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, genreName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("GenreID");
                }
            }
        }
        return 0;
    }

    public int getCountryIdByName(String countryName) throws SQLException {
        String query = "SELECT CountryID FROM Country WHERE CountryName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, countryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CountryID");
                }
            }
        }
        return 0;
    }
}