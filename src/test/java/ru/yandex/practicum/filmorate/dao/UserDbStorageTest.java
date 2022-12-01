package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    public void fieldsUserShouldBeCorrect() {
        User user = User.builder()
                .id(1L)
                .email("lisaann@ya.ru")
                .login("gape")
                .name("Lisa")
                .birthday(LocalDate.of(1972, 5, 9))
                .build();

        user.setName(user.getLogin());

        Optional<User> optionalUser = Optional.of(userDbStorage.create(user));

        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(o ->
                        assertThat(o).hasFieldOrPropertyWithValue("email", "lisaann@ya.ru"));

        Optional<User> optionalUserById = Optional.of(userDbStorage.findById(1L));

        assertThat(optionalUserById)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", 1L)
                );

        User testUpdateUser = User.builder()
                .email("lisaann@ya.ru")
                .login("gape")
                .birthday(LocalDate.of(1972, 5, 9))
                .build();
        testUpdateUser.setName(testUpdateUser.getLogin());
        testUpdateUser.setId(1L);

        Optional<User> testUserUpdateOptional = Optional.of(userDbStorage.update(testUpdateUser));

        assertThat(testUserUpdateOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "gape")
                );
    }
}