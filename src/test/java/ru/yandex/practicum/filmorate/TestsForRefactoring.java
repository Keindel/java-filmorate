

package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.DirectorValidationException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MarkValidationException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.util.RepositoryCleaner;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TestsForRefactoring {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;

    private final RepositoryCleaner repositoryCleaner;
    private final JdbcTemplate jdbcTemplate;

    private static HashSet<Genre> filmGenres;
    private ArrayList<Director> directors;
    private Director director;

    @BeforeAll
    public static void beforeAll() {
        filmGenres = new HashSet<>();
        filmGenres.add(new Genre(6,"Боевик"));
    }

    @BeforeEach
    public void beforeEach() throws DirectorValidationException, UserNotFoundException, FilmNotFoundException, MarkValidationException {
        repositoryCleaner.clean();
        populateDb();
    }

    private void populateDb() throws DirectorValidationException, UserNotFoundException, FilmNotFoundException, MarkValidationException {
        userDbStorage.create(User.builder()
                .email("abc@mail.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1011, 1, 1))
                .build());
        userDbStorage.create(User.builder()
                .email("abc2@mail.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(2022, 2, 2))
                .build());
        HashSet<Genre> filmGenres = new HashSet<>();
        filmGenres.add(new Genre(6,"Боевик"));

        director = new Director(1, "Tom");
        directors = new ArrayList<>();
        directors.add(director);
        directorService.create(director);

        filmDbStorage.create(Film.builder()
                .name("Never again")
                .description("Amazing film").releaseDate(LocalDate.of(2000,1,1))
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(120).genres(filmGenres)
                .mpa(new Mpa(1, "G"))
                .genres(filmGenres)
                .usersIdsMarks(new HashMap<>())
                .directors(directors)
                .build());
        filmDbStorage.create(Film.builder()
                .name("Always again")
                .description("Funny film")
                .releaseDate(LocalDate.of(2000,1,1))
                .duration(120).genres(filmGenres)
                .mpa(new Mpa(1, "G"))
                .genres(filmGenres)
                .usersIdsMarks(new HashMap<>())
                .directors(directors)
                .build());

        filmService.markFromUser(1L,1L,2);
        filmService.markFromUser(1L,2L,2);
        filmService.markFromUser(2L,1L,10);
        filmService.markFromUser(2L,2L,10);
    }

    @Test
    public void testAddMarkFromUser() {
        String sqlQuery = "select * from marks";
        List<List<Long>> arrays = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
                    List<Long> array = new ArrayList<>();
                    array.add (rs.getLong("film_id"));
                    array.add (rs.getLong("mark_from_user"));
                    array.add(rs.getLong("mark"));
                    return array;
                }
        );
        List<List<Long>> listsForTests = new ArrayList<>();
        List<Long> list = new ArrayList<>();

        list.add(1L);
        list.add(1L);
        list.add(2L);
        listsForTests.add(list);
        list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(2L);
        listsForTests.add(list);

        Assertions.assertEquals(listsForTests.get(1), arrays.get(1));
    }

    @Test
    public void testAddMarkToNotExistedFilmFromUser() {
        assertThrows(
                FilmNotFoundException.class, () -> filmService.markFromUser(4L,1L,2)
        );
    }

    @Test
    public void testAddMarkFromNotExistedUser() {
        assertThrows(
                UserNotFoundException.class, () -> filmService.markFromUser(1L,8L,2)
        );
    }

    @Test
    public void wrongMarkValidationTest(){
        assertThrows(
                MarkValidationException.class, () -> filmService.markFromUser(1L,1L,0)
        );
    }

    @Test
    public void testUnmarkFromUser() throws UserNotFoundException, FilmNotFoundException {
        filmService.unmarkFromUser(1L,2L);
        filmService.unmarkFromUser(2L,1L);
        filmService.unmarkFromUser(2L,2L);

        String sqlQuery = "select * from marks";
        List<List<Long>> arrays = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
                    List<Long> array = new ArrayList<>();
                    array.add (rs.getLong("film_id"));
                    array.add (rs.getLong("mark_from_user"));
                    array.add(rs.getLong("mark"));
                    return array;
                }
        );

        List<List<Long>> listsForTests = new ArrayList<>();
        List<Long> list = new ArrayList<>();

        list.add(1L);
        list.add(1L);
        list.add(2L);
        listsForTests.add(list);

        Assertions.assertEquals(listsForTests, arrays);
    }

    @Test
    public void testUnmarkFromNotExistedUser() {
        assertThrows(
                UserNotFoundException.class, () -> filmService.unmarkFromUser(1L,8L)
        );
    }

    @Test
    public void testUnmarkNotExistedFilmFromUser() {
        assertThrows(
                FilmNotFoundException.class, () -> filmService.unmarkFromUser(8L,1L)
        );
    }

    @Test
    public void testMostPopularFilms() throws UserNotFoundException, FilmNotFoundException, MarkValidationException, DirectorValidationException {
        Film film3 = filmDbStorage.create(Film.builder()
                .name("Crazy")
                .description("Amazing film").releaseDate(LocalDate.of(2001, 1, 1))
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120).genres(filmGenres)
                .mpa(new Mpa(1, "G"))
                .genres(filmGenres)
                .usersIdsMarks(new HashMap<>())
                .directors(directors)
                .build());
        Film film4 = filmDbStorage.create(Film.builder()
                .name("Happy")
                .description("Funny film")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120).genres(filmGenres).mpa(new Mpa(1, "G"))
                .mpa(new Mpa(1, "G"))
                .genres(filmGenres)
                .usersIdsMarks(new HashMap<>())
                .directors(directors)
                .build());

        filmService.markFromUser(3L,1L,2);
        filmService.markFromUser(3L,2L,2);
        filmService.markFromUser(4L,1L,9);
        filmService.markFromUser(4L,2L,10);

        List<Film> list = new ArrayList<>();
        list.add(film4);
        list.add(film3);
        List<Film> filmsList = (List<Film>) filmService.mostPopularFilms(10,2001,6);
        Optional<Film> filmOptional = Optional.of(filmsList.get(0));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("rating", 9.5)
                );
        Assertions.assertEquals(list, filmService.mostPopularFilms(10,2001,6));
        Assertions.assertEquals(new ArrayList<>(), filmService.mostPopularFilms(10,2001,3));
    }

    @Test
    public void testMostPopularFilmsOfEmptyYear() {
        Assertions.assertEquals(new ArrayList<>(), filmService.mostPopularFilms(10,3001,6));
    }

    @Test
    void recommendFilmsForUserTest() throws UserNotFoundException, FilmNotFoundException, MarkValidationException {
        Film film3 = filmDbStorage.create(Film.builder() // filmNo.3
                .name("Again and again")
                .description("Amazing film!").releaseDate(LocalDate.of(2000, 1, 1))
                .releaseDate(LocalDate.of(2000, 2, 1))
                .duration(120).genres(filmGenres)
                .mpa(new Mpa(1, "G"))
                .genres(filmGenres)
                .usersIdsMarks(new HashMap<>())
                .directors(directors)
                .build());

        Film film4 = filmDbStorage.create(Film.builder() // filmNo.3
                .name("Again and again 2")
                .description("Amazing film! Second part!").releaseDate(LocalDate.of(2000, 1, 1))
                .releaseDate(LocalDate.of(2005, 2, 1))
                .duration(125).genres(filmGenres)
                .mpa(new Mpa(1, "G"))
                .genres(filmGenres)
                .usersIdsMarks(new HashMap<>())
                .directors(directors)
                .build());

        userDbStorage.create(User.builder() // userNo.3
                .email("abc212@mail.ru")
                .login("login3")
                .name("name3")
                .birthday(LocalDate.of(2022, 2, 2))
                .build());

        userDbStorage.create(User.builder() // userNo.4
                .email("abc42@mail.ru")
                .login("login4")
                .name("name4")
                .birthday(LocalDate.of(2022, 2, 2))
                .build());

        userDbStorage.create(User.builder() // userNo.4
                .email("abc52@mail.ru")
                .login("login54")
                .name("name5")
                .birthday(LocalDate.of(2022, 2, 2))
                .build());

        filmService.markFromUser(2L, 3L, 9);
        filmService.markFromUser(3L, 1L, 6);
        filmService.markFromUser(1L,4L,2);
        filmService.markFromUser(2L,4L,10);
        filmService.markFromUser(4L,3L,10);
        List<Film> resultList = (List<Film>) userService.recommendFilmsForUser(3L);
        Assertions.assertEquals(film3, resultList.get(0));
        resultList = (List<Film>) userService.recommendFilmsForUser(1L);
        Assertions.assertEquals(film4, resultList.get(0));
        ArrayList<Film> films = new ArrayList();
        films.add(film3);
        films.add(film4);
        Assertions.assertEquals(films, userService.recommendFilmsForUser(2L));
        Assertions.assertTrue(userService.recommendFilmsForUser(5L).isEmpty());
    }

}