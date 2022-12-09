package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaController {

    private final FilmService filmService;

    @GetMapping
    public List<Mpa> findAll() {
        return filmService.getMpaRatings();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable int id) {
        log.debug("Start request GET to /mpa/, with id = {}", id);
        return filmService.getMpaRatingById(id);
    }
}