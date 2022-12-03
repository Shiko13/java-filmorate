package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable long id) {
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        throwIfNotValid(director);
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        throwIfNotValid(director);
        return directorService.update(director);
    }

    @DeleteMapping
    public void deleteAll() {
        directorService.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        directorService.deleteById(id);
    }

    public void throwIfNotValid(Director director) {
        log.debug("Start validation of director");

        if (director.getName() == null || director.getName().isBlank()) {
            log.warn("Unsuccessful validation of director");
            throw new ValidateException("Name should not be empty");
        }
    }
}
