package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private final LocalDate BIRTHDAY_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Получен запрос к эндпоинту /films, метод GET");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос к эндпоинту /films, метод POST");
        if (!films.containsValue(film)) {
            validationFilm(film);
        } else {
            throw new ValidateException("Этот фильм уже есть в фильморейтинге :)");
        }
        film.setId(id++);
        log.info("Добавление нового фильма");
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос к эндпоинту /films, метод PUT");
        if (!films.containsKey(film.getId())) {
            throw new ValidateException("Фильм с id " + film.getId() + " не найден");
        }
        validationFilm(film);
        log.info("Обновление фильма");
        films.put(film.getId(), film);
        return film;
    }

    public void validationFilm(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMATOGRAPHY)) {
            throw new ValidateException("Братья Люмьер смотрят на вас с недоумением! (кажется, вы ошиблись с датой");
        }
        log.info("Валидация пройдена");
    }
}
