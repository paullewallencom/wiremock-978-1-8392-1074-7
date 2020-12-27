package com.learnwiremock.service;

import com.learnwiremock.constants.MoviesAppConstants;
import com.learnwiremock.dto.Movie;
import com.learnwiremock.exception.MovieErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class MoviesRestClient {

    private WebClient webClient;

    public MoviesRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Movie> retrieveAllMovies() {

        //http://localhost:8081/movieservice/v1/allMovies

        return webClient.get().uri(MoviesAppConstants.GET_ALL_MOVIES_V1)
                .retrieve()
                .bodyToFlux(Movie.class)
                .collectList()
                .block();
    }

    public Movie retrieveMovieById(Integer movieId) {

        //http://localhost:8081/movieservice/v1/movie/100
        try {
            return webClient.get().uri(MoviesAppConstants.RETRIEVE_MOVIE_BY_ID, movieId)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException in retrieveMovieById. Status code is {} and the message is {} ", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception in retrieveMovieById and the message is {} ", ex);
            throw new MovieErrorResponse(ex);
        }
    }

    public List<Movie> retrieveMoviebyName(String name) {

//        http://localhost:8081/movieservice/v1/movieName?movie_name=ABC

        String retrieveByNameUri = UriComponentsBuilder.fromUriString(MoviesAppConstants.MOVIE_BY_NAME_QUERY_PARAM_V1)
                .queryParam("movie_name", name)
                .buildAndExpand()
                .toUriString();
        try {
            return webClient.get().uri(retrieveByNameUri)
                    .retrieve()
                    .bodyToFlux(Movie.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException in retrieveMoviebyName. Status code is {} and the message is {} ", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception in retrieveMoviebyName and the message is {} ", ex);
            throw new MovieErrorResponse(ex);
        }

    }

    public List<Movie> retrieveMoviebyYear(Integer movieYear) {

//        http://localhost:8081/movieservice/v1/movieYear?year=1950

        String retrieveByNameUri = UriComponentsBuilder.fromUriString(MoviesAppConstants.MOVIE_BY_YEAR_QUERY_PARAM_V1)
                .queryParam("year", movieYear)
                .buildAndExpand()
                .toUriString();
        try {
            return webClient.get().uri(retrieveByNameUri)
                    .retrieve()
                    .bodyToFlux(Movie.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("WebClientResponseException in retrieveMoviebyYear. Status code is {} and the message is {} ", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            throw new MovieErrorResponse(ex.getStatusText(), ex);
        } catch (Exception ex) {
            log.error("Exception in retrieveMoviebyYear and the message is {} ", ex);
            throw new MovieErrorResponse(ex);
        }

    }
}
