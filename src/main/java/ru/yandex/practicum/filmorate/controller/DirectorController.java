package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable long id) {
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
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
}
