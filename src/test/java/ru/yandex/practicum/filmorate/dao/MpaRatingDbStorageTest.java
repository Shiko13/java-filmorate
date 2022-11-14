package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRatingDbStorageTest {
    private final MpaRatingDbStorage mpaRatingDbStorage;

    @Test
    public void getMpaTest() {
        Optional<Mpa> optionalMpa = Optional.of(mpaRatingDbStorage.readById(1));

        assertThat(optionalMpa)
                .isPresent()
                .hasValueSatisfying(m ->
                        assertThat(m).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "G")
                );
    }
}
