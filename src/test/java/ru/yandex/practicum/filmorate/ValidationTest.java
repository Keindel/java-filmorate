package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationTest {

    static FilmController filmController;
    static UserController userController;


    @BeforeAll
    public static void beforeAll() {
        filmController = new FilmController();
        userController = new UserController();
    }

    @BeforeEach

    @AfterEach

    @AfterAll


    @Test
    public void shouldValidate() throws FilmValidationException, UserValidationException, IOException, InterruptedException {
        Film okFilm = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();
        filmController.create(okFilm);
        Collection<Film> films = filmController.findAll();
        assertTrue(films.contains(okFilm));

        assertThrows(FilmValidationException.class, () -> filmController.create(null));
        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build()));
        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1022, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build()));
        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(Duration.ofMinutes(-120))
                .build()));
        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
                .build()));


        User okUser = User.builder()
                .id(1)
                .email("valid@gmail.com")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2022, 2, 2))
                .build();
        userController.create(okUser);
        Collection<User> users = userController.findAll();
        assertTrue(users.contains(okUser));

        assertThrows(FilmValidationException.class, () -> userController.create(null));
        assertThrows(FilmValidationException.class, () -> userController.create(User.builder()
                .id(1)
                .email("valid@gmail.com")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2022, 2, 2))
                .build()));
        assertThrows(FilmValidationException.class, () -> userController.create(User.builder()
                .id(1)
                .email("gmail.com")
                .login("lo gin")
                .name("")
                .birthday(LocalDate.of(2022, 2, 2))
                .build()));
        assertThrows(FilmValidationException.class, () -> userController.create(User.builder()
                .id(1)
                .email("valid@gmail.com")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2032, 2, 2))
                .build()));
        assertThrows(FilmValidationException.class, () -> userController.create(User.builder()
                .build()));


//        ConfigurableApplicationContext ctx = SpringApplication.run(FilmorateApplication.class);
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpResponse<String> httpResponse = client.send(HttpRequest.newBuilder()
//                        .uri(URI.create("http://localhost:8081/users"))
//                        .header("Content-Type", "application/json")
//                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
//                        .build()
//                , HttpResponse.BodyHandlers.ofString());
//
//        HttpResponse<String> httpResponse = client.send(HttpRequest.newBuilder()
//                        .uri(URI.create("http://localhost:8081/users"))
//                        .header("Content-Type", "application/json")
//                        .GET()
//                        .build()
//                , HttpResponse.BodyHandlers.ofString());
//
//        HttpResponse<String> httpResponse = client.send(HttpRequest.newBuilder()
//                        .uri(URI.create("http://localhost:8081/users"))
//                        .header("Content-Type", "application/json")
//                        .GET()
//                        .build()
//                , HttpResponse.BodyHandlers.ofString());
//
//
//        ctx.close();
    }

}
