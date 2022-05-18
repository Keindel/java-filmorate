//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.*;
//import ru.yandex.practicum.filmorate.controllers.FilmController;
//import ru.yandex.practicum.filmorate.controllers.UserController;
//import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
//import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.Collection;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class ValidationTest {
//
//    static FilmController filmController;
//    static UserController userController;
//
//    @BeforeAll
//    public static void beforeAll() {
//        filmController = new FilmController(filmStorage);
//        userController = new UserController();
//    }
//
//    @Test
//    public void shouldValidate() throws FilmValidationException, UserValidationException, IOException, InterruptedException {
//        Film okFilm = Film.builder()
//                .name("name")
//                .description("description")
//                .releaseDate(LocalDate.of(2022, 1, 1))
//                .duration(Duration.ofMinutes(120))
//                .build();
//        filmController.create(okFilm);
//        Collection<Film> films = filmController.findAll();
//        assertTrue(films.contains(okFilm));
//
//        assertThrows(FilmValidationException.class, () -> filmController.create(null));
//        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
//                .name("")
//                .description("description")
//                .releaseDate(LocalDate.of(2022, 1, 1))
//                .duration(Duration.ofMinutes(120))
//                .build()));
//        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
//                .name("name")
//                .description("description")
//                .releaseDate(LocalDate.of(1022, 1, 1))
//                .duration(Duration.ofMinutes(120))
//                .build()));
//        assertThrows(FilmValidationException.class, () -> filmController.create(Film.builder()
//                .name("")
//                .description("description")
//                .releaseDate(LocalDate.of(2022, 1, 1))
//                .duration(Duration.ofMinutes(-120))
//                .build()));
//        assertThrows(NullPointerException.class, () -> filmController.create(Film.builder()
//                .build()));
//
//
//        User okUser = User.builder()
//                .email("valid@gmail.com")
//                .login("login")
//                .name("")
//                .birthday(LocalDate.of(2022, 2, 2))
//                .build();
//        userController.create(okUser);
//        Collection<User> users = userController.findAll();
//        assertTrue(users.contains(okUser));
//
//        assertThrows(UserValidationException.class, () -> userController.create(null));
//        assertThrows(UserValidationException.class, () -> userController.create(User.builder()
//                .email("valid@gmail.com")
//                .login("login")
//                .name("")
//                .birthday(LocalDate.of(2022, 2, 2))
//                .build()));
//        assertThrows(UserValidationException.class, () -> userController.create(User.builder()
//                .email("gmail.com")
//                .login("lo gin")
//                .name("")
//                .birthday(LocalDate.of(2022, 2, 2))
//                .build()));
//        assertThrows(UserValidationException.class, () -> userController.create(User.builder()
//                .email("valid@gmail.com")
//                .login("login")
//                .name("")
//                .birthday(LocalDate.of(2032, 2, 2))
//                .build()));
//        assertThrows(NullPointerException.class, () -> userController.create(User.builder()
//                .build()));
//    }
//}
