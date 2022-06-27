package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.util.RepositoryCleaner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final RepositoryCleaner repositoryCleaner;

    @BeforeEach
    public void beforeEach() {
        repositoryCleaner.clean();
        populateUserDb();
    }

    private void populateUserDb() {
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
    }

    @Test
    public void testFindUsersByIds() throws UserNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getById(1L));
        Optional<User> userOptional2 = Optional.ofNullable(userDbStorage.getById(2L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertThat(userOptional2)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void testGetSizeOfUsers() {
        Optional<Long> sizeOptional = Optional.of(userDbStorage.getSize());

        assertThat(sizeOptional)
                .isPresent()
                .hasValue(2L);
    }

    @Test
    public void testFindAllUsers() {
        Optional<Collection<User>> collectionOptional = Optional.of(userDbStorage.findAll());

        assertThat(collectionOptional)
                .isPresent()
                .hasValueSatisfying(col ->
                        assertThat(col).hasOnlyElementsOfType(User.class).hasSize(2));
    }

    @Test
    public void testUpdateUser() throws UserNotFoundException {
        userDbStorage.update(User.builder()
                .id(2)
                .email("updabc2@mail.ru")
                .login("updlogin2")
                .name("updname2")
                .birthday(LocalDate.of(4044, 4, 4))
                .build());

        assertThat(userDbStorage.getById(2L))
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("email", "updabc2@mail.ru")
                .hasFieldOrPropertyWithValue("login", "updlogin2")
                .hasFieldOrPropertyWithValue("name", "updname2")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(4044, 4, 4));
    }

    @Test
    public void testDeleteUserById() throws UserNotFoundException {
        userDbStorage.deleteById(2L);

        Optional<Collection<User>> collectionOptional = Optional.of(userDbStorage.findAll());
        assertThat(collectionOptional)
                .isPresent()
                .hasValueSatisfying(col ->
                        assertThat(col).hasOnlyElementsOfType(User.class)
                                .hasSize(1));

        assertThat(collectionOptional)
                .isPresent()
                .hasValueSatisfying(col ->
                        assertThat(col).hasOnlyElementsOfType(User.class)
                                .containsOnly(User.builder()
                                        .id(1)
                                        .email("abc@mail.ru")
                                        .login("login1")
                                        .name("name1")
                                        .birthday(LocalDate.of(1011, 1, 1))
                                        .build()));
    }
}
