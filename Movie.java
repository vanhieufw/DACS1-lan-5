package com.movie.model;

import java.sql.Date;

public class Movie {
    private int movieID;
    private String title;
    private String description;
    private int duration;
    private String director;
    private int genreID;
    private String genreName;
    private String poster;
    private Date startDate;
    private Date endDate;
    private int productionYear;
    private int countryID;
    private String countryName;
    private int ageRestriction;

    // Constructors
    public Movie() {}

    // Getters and Setters
    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public int getGenreID() { return genreID; }
    public void setGenreID(int genreID) { this.genreID = genreID; }
    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public int getProductionYear() { return productionYear; }
    public void setProductionYear(int productionYear) { this.productionYear = productionYear; }
    public int getCountryID() { return countryID; }
    public void setCountryID(int countryID) { this.countryID = countryID; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public int getAgeRestriction() { return ageRestriction; }
    public void setAgeRestriction(int ageRestriction) { this.ageRestriction = ageRestriction; }
}