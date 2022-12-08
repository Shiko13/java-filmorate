package ru.yandex.practicum.filmorate.service.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getAll() {
        log.debug("Start request GET to /directors");

        return directorStorage.readAll();
    }

    @Override
    public Director getById(long id) {
        log.debug("Start request GET to /directors/{}", id);

        return directorStorage.readById(id);
    }

    @Override
    public Director create(Director director) {
        log.debug("Start request POST to /directors, with id = {}, name = {}, surname = {}",
                director.getId(), director.getName(), director.getSurname());

        return directorStorage.create(director);
    }

    @Override
    public Director update(Director director) {
        log.debug("Start request PUT to /directors, with id = {}, name = {}, surname = {}",
                director.getId(), director.getName(), director.getSurname());

        return directorStorage.update(director);
    }

    @Override
    public void deleteById(long id) {
        log.debug("Start request DELETE to /directors/{}", id);

        directorStorage.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Start request DELETE to /users)");

        directorStorage.deleteAll();
    }
}
