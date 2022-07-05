package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collections;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
@Sql(scripts = {"/schemaForTests.sql", "/dataForTests.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FilmControllerTest {
    Film film = Film.builder()
        .name("Matrix")
        .description("Follow the white rabbit")
        .releaseDate(LocalDate.of(1999, 3, 31))
        .duration(136)
        .mpa(new Mpa(4, null))
        .genres(new TreeSet<>(Collections.singleton(new Genre(6, null))))
        .build();
    @Autowired
    private FilmController controller;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;


    @Test
    public void contextLoads2() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldReturnEmptyListOfFilms() throws Exception {
        this.mockMvc.perform(get("/films"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnFilm() throws Exception {
        String jacksonFilm = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films")
                .content(jacksonFilm)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print());
        this.mockMvc.perform(get("/films"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].name").value("Matrix"))
            .andExpect(jsonPath("$[0].description").value("Follow the white rabbit"))
            .andExpect(jsonPath("$[0].releaseDate").value("1999-03-31"))
            .andExpect(jsonPath("$[0].duration").value("136"))
            .andExpect(jsonPath("$[0].mpa.name").value("R"))
        ;
    }

    @Test
    public void shouldCreateAndReturnMatrixMovie() throws Exception {
        String jacksonFilm = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films")
                .content(jacksonFilm)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Matrix"))
            .andExpect(jsonPath("$.description").value("Follow the white rabbit"))
            .andExpect(jsonPath("$.releaseDate").value("1999-03-31"))
            .andExpect(jsonPath("$.duration").value("136"))
            .andExpect(jsonPath("$.mpa.id").value("4"))
            .andExpect(jsonPath("$.mpa.name").value("R"))
            .andExpect(jsonPath("$.genres[0].id").value("6"))
            .andExpect(jsonPath("$.genres[0].name").value("Боевик"));
    }

    @Test
    public void shouldReturnCode400WithFailName() throws Exception {
        String filmWithFailName = "{" +
            "\"name\":\" \"," +
            "\"description\":\"The best movie\"," +
            "\"releaseDate\":\"1999-03-31\"," +
            "\"duration\":136}";
        this.mockMvc.perform(post("/films")
                .content(filmWithFailName).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnCode400WithFailDescription() throws Exception {
        String filmWithFailDescription = "{" +
            "\"name\":\"Matrix\"," +
            "\"description\":\" \"," +
            "\"releaseDate\":\"1999-03-31\"," +
            "\"duration\":136}";
        this.mockMvc.perform(post("/films").
                content(filmWithFailDescription).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnCode400WithFailReleaseDate() throws Exception {
        String filmWithFailReleaseDate = "{" +
            "\"name\":\"Matrix\"," +
            "\"description\":\"The best movie\"," +
            "\"releaseDate\":\"1895-12-27\"," +
            "\"duration\":136}";
        this.mockMvc.perform(post("/films")
                .content(filmWithFailReleaseDate).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnCode400WithFailDuration() throws Exception {
        String filmWithFailDuration = "{" +
            "\"name\":\"Matrix\"," +
            "\"description\":\"The best movie\"," +
            "\"releaseDate\":\"1999-03-31\"," +
            "\"duration\":-136}";
        this.mockMvc.perform(post("/films")
                .content(filmWithFailDuration).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldChangeFilm() throws Exception {
        String jacksonFilm = mapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(jacksonFilm).contentType(MediaType.APPLICATION_JSON))
            .andDo(print());
        String updatedFilm = "{\"id\":1," +
            "\"name\":\"MatrixUpdate\"," +
            "\"description\":\"He is the chosen one\"," +
            "\"releaseDate\":\"1999-03-30\"," +
            "\"duration\":140," +
            "\"mpa\":{\"id\":5,\"name\":null}," +
            "\"genres\":[{\"id\":6,\"name\":null},{\"id\":4,\"name\":null}]," +
            "\"likes\":null}";
        this.mockMvc.perform(put("/films").content(updatedFilm).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        this.mockMvc.perform(get("/films/{id}", 1))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("MatrixUpdate"))
            .andExpect(jsonPath("$.description").value("He is the chosen one"))
            .andExpect(jsonPath("$.releaseDate").value("1999-03-30"))
            .andExpect(jsonPath("$.duration").value("140"))
            .andExpect(jsonPath("$.mpa.id").value("5"))
            .andExpect(jsonPath("$.mpa.name").value("NC-17"))
            .andExpect(jsonPath("$.genres[0].id").value("4"))
            .andExpect(jsonPath("$.genres[0].name").value("Триллер"))
            .andExpect(jsonPath("$.genres[1].id").value("6"))
            .andExpect(jsonPath("$.genres[1].name").value("Боевик"));
    }
}
