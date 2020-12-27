package com.learnwiremock.service;

import com.github.jenspiegsa.wiremockextension.ConfigureWireMock;
import com.github.jenspiegsa.wiremockextension.InjectServer;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.Options;
import com.learnwiremock.dto.Movie;
import com.learnwiremock.exception.MovieErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(WireMockExtension.class)
public class MoviesRestClientTest {

    MoviesRestClient moviesRestClient;
    WebClient webClient;

    @InjectServer
    WireMockServer wireMockServer;

    @ConfigureWireMock
    Options options = wireMockConfig().
            port(8088)
            .notifier(new ConsoleNotifier(true));

    @BeforeEach
    void setUp(){

        String baseUrl = "http://localhost:8081";
        webClient = WebClient.create(baseUrl);
        moviesRestClient = new MoviesRestClient(webClient);
    }

    @Test
    void retrieveAllMovies(){

        //when
        List<Movie> movieList = moviesRestClient.retrieveAllMovies();
        System.out.println("movieList : " + movieList);

        //then
        assertTrue(movieList.size()>0);
    }

    @Test
    void retrieveMovieById() {
        //given
        Integer movieId =1;

        //when
        Movie movie = moviesRestClient.retrieveMovieById(movieId);

        //then
        assertEquals("Batman Begins", movie.getName());

    }

    @Test
    void retrieveMovieById_notFound(){
        //given
        Integer movieId =100;

        //when
        Assertions.assertThrows(MovieErrorResponse.class,()-> moviesRestClient.retrieveMovieById(movieId));

    }

    @Test
    void retrieveMoviebyName(){

        //given
        String movieName = "Avengers";

        //when
        List<Movie> movieList = moviesRestClient.retrieveMoviebyName(movieName);

        //then
        String castExpected = "Robert Downey Jr, Chris Evans , Chris HemsWorth";
        assertEquals(4, movieList.size());
        assertEquals(castExpected, movieList.get(0).getCast());

    }

    @Test
    void retrieveMoviebyName_Not_Found(){

        //given
        String movieName = "ABC";

        //when
        Assertions.assertThrows(MovieErrorResponse.class,()-> moviesRestClient.retrieveMoviebyName(movieName));

    }

    @Test
    void retrieveMoviebyYear(){
        //given
        Integer year = 2012;

        //when
        List<Movie> movieList = moviesRestClient.retrieveMoviebyYear(year);

        //then
        assertEquals(2, movieList.size());

    }

    @Test
    void retrieveMoviebyYear_not_found(){
        //given
        Integer year = 1950;

        //when
        Assertions.assertThrows(MovieErrorResponse.class,()->moviesRestClient.retrieveMoviebyYear(year));

    }

    @Test
    void addMovie() {
        //given
        Movie movie = new Movie(null,"Toys Story 4", "Tom Hanks, Tim Allen",2019, LocalDate.of(2019, 06, 20));

        //when
        Movie addedMovie = moviesRestClient.addMovie(movie);
        System.out.println(addedMovie);

        //then
        assertTrue(addedMovie.getMovie_id()!=null);
    }

    @Test
    void addMovie_badRequest() {
        //given
        Movie movie = new Movie(null,null, "Tom Hanks, Tim Allen",2019, LocalDate.of(2019, 06, 20));

        //when
        String expectedErrorMessage = "Please pass all the input fields : [name]";
        Assertions.assertThrows(MovieErrorResponse.class, ()-> moviesRestClient.addMovie(movie), expectedErrorMessage);
    }

    @Test
    void updateMovie() {
        //given
        Integer movieId=3;
        String cast = "ABC";
        Movie movie = new Movie(null,null, cast,null, null);

        //when
        Movie updatedMovie = moviesRestClient.updateMovie(movieId, movie);

        //then
        assertTrue(updatedMovie.getCast().contains(cast));
    }

    @Test
    void updateMovie_notFound() {
        //given
        Integer movieId=100;
        String cast = "ABC";
        Movie movie = new Movie(null,null, cast,null, null);

        //then
        Assertions.assertThrows(MovieErrorResponse.class, ()-> moviesRestClient.updateMovie(movieId, movie));
    }

    @Test
    void deleteMovie() {
        //given
        Movie movie = new Movie(null,"Toys Story 5", "Tom Hanks, Tim Allen",2019, LocalDate.of(2019, 06, 20));
        Movie addedMovie = moviesRestClient.addMovie(movie);

        //when
        String responseMessage = moviesRestClient.deleteMovie(addedMovie.getMovie_id().intValue());

        //then
        String expectedErrorMessage = "Movie Deleted Successfully";
        assertEquals(expectedErrorMessage, responseMessage);
    }

    @Test
    void deleteMovie_NotFound() {
        //given
        Integer id = 100;

        //then
        Assertions.assertThrows(MovieErrorResponse.class, ()-> moviesRestClient.deleteMovie(id));

    }
}

