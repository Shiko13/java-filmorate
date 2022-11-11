package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAllFilms() {
        return filmService.getAll();
    }

    @GetMapping("films/{id}")
    public Film findFilmById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @GetMapping("films/popular")
    public Collection<Film> showTopMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopMostLikedFilms(count);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film changeFilm(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("films/{id}/like/{userId}")
    public void addLike(@PathVariable long id,
                        @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("films/{id}")
    public void deleteFilmById(@PathVariable long id) {
        filmService.deleteById(id);
    }

    @DeleteMapping("/films")
    public void deleteAllFilms() {
        filmService.deleteAll();
    }

    @GetMapping("/mpa")
    public Collection<Mpa> findAllMpaRatings() {
        return filmService.getMpaRatings();
    }

    @GetMapping("/mpa/{id}")
    public Mpa findMpaRatingById(@PathVariable int id) {
        log.debug("Start request GET to /mpa/, with id = {}", id);
        return filmService.getMpaRatingById(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> findAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable long id) {
        return filmService.getGenreById(id);
    }
}