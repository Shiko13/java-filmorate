package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmController {

    private final FilmService filmService;
    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public Set<Film> getTopPopular(@RequestParam (required = false) @Positive Long genreId,
                                   @RequestParam (name = "year", required = false) @Min(1895) Integer releaseYear,
                                   @RequestParam (defaultValue = "10") @Positive int count) {
        return filmService.getTopPopular(genreId, releaseYear, count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirector(@PathVariable long directorId,
                                    @RequestParam("sortBy") FilmSortBy filmSortBy) {
        return filmService.getSortListByDirector(directorId, filmSortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilmsByTitleByDirector(@RequestParam(required = false) String query,
                                                  @RequestParam(required = false) String by) {
        return filmService.searchFilmsByTitleByDirector(query, by);
    }

    @GetMapping("/common")
    public Set<Film> getCommon(@RequestParam @Positive long userId,
                               @RequestParam @Positive long friendId) {

        return filmService.getCommon(userId, friendId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film change(@Valid @RequestBody Film film) {
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
}
