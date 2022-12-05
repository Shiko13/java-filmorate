package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmController {

    private final LocalDate BIRTHDAY_OF_CINEMATOGRAPHY = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopPopular(@RequestParam (defaultValue = "-1") Long genreId,
                                          @RequestParam(name = "year", defaultValue = "-1") Integer releaseYear,
                                          @RequestParam (defaultValue = "10") @Positive int count) {
        if (releaseYear > 0) throwIfNotValidDate(LocalDate.of(releaseYear, 12, 28));

        return filmService.getTopPopular(genreId, releaseYear, count);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getByDirector(@PathVariable long directorId,
                                          @RequestParam String sortBy) {
        return filmService.getSortListByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilmsByTitleByDirector(@RequestParam(required = false) String query,
                                                  @RequestParam(required = false) String by) {
        return filmService.searchFilmsByTitleByDirector(query, by);
    }

    @GetMapping("/common")
    public Collection<Film> getCommon(@RequestParam long userId,
                                      @RequestParam long friendId) {
        if (userId == 0 || friendId == 0) throw new ValidateException("Необходимо заполнить id обоих пользователей");

        return filmService.getCommon(userId, friendId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        throwIfNotValidDate(film);

        return filmService.create(film);
    }

    @PutMapping
    public Film change(@Valid @RequestBody Film film) {
        throwIfNotValidDate(film);

        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id,
                        @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        filmService.deleteById(id);
    }

    @DeleteMapping
    public void deleteAll() {
        filmService.deleteAll();
    }

    public void throwIfNotValidDate(Film film) {
        log.debug("Start validation of film");

        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMATOGRAPHY)) {
            throw new ValidateException("Lumiere brothers look at you with surprise! (to much early date)");
        }

        log.debug("Validation successful passed");
    }

    public void throwIfNotValidDate(LocalDate releaseYear) {
        log.debug("Start validation of film");

        if (releaseYear.isBefore(BIRTHDAY_OF_CINEMATOGRAPHY)) {
            throw new ValidateException("Lumiere brothers look at you with surprise! (to much early date)");
        }

        log.debug("Validation successful passed");
    }
}