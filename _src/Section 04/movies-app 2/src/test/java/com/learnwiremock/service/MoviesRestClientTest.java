package com.learnwiremock.service;

import com.learnwiremock.dto.Movie;
import com.learnwiremock.exception.MovieErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesRestClientTest {

    MoviesRestClient moviesRestClient;
    WebClient webClient;

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

}

