package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendshipDbStorageTest {
    private final FriendshipDbStorage friendshipDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    public void addFriendShouldBeCorrect() {
        User user1 = User.builder()
                .id(1L)
                .email("lisaann@ya.ru")
                .login("gape")
                .name("Lisa")
                .birthday(LocalDate.of(1972, 5, 9))
                .build();

        userDbStorage.createUser(user1);

        User user2 = User.builder()
                .id(2L)
                .email("lindalovelace@ya.ru")
                .login("ll")
                .name("Linda")
                .birthday(LocalDate.of(1949, 1, 10))
                .build();

        userDbStorage.createUser(user2);

        Optional<Friendship> optionalFriendship =
                Optional.of(friendshipDbStorage.create(1L, 2L));

        assertThat(optionalFriendship)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("userOneId", 1L)
                                .hasFieldOrPropertyWithValue("userTwoId", 2L)
                );
    }
}
