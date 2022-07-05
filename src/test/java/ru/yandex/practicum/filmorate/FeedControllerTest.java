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
public class FeedControllerTest {
    Film film = Film.builder()
        .name("labore nulla")
        .description("Duis in consequat esse")
        .releaseDate(LocalDate.of(1979, 4, 17))
        .duration(100)
        .mpa(new Mpa(1, null))
        .genres(new TreeSet<>(Collections.singleton(new Genre(1, null))))
        .build();
    @Autowired
    private FilmController controller;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldReturnUser1GetFeed() throws Exception {
        this.mockMvc.perform(get("/users/{id}/feed", 1))
            .andDo(print())
            .andExpect(status().isOk());
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
            .andExpect(jsonPath("$[0].name").value("labore nulla"))
            .andExpect(jsonPath("$[0].description").value("Duis in consequat esse"))
            .andExpect(jsonPath("$[0].releaseDate").value("1979-04-17"))
            .andExpect(jsonPath("$[0].duration").value("100"))
            .andExpect(jsonPath("$[0].mpa.name").value("G"))
        ;
    }
}
