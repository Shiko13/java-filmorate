package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface Crud<T> {
    List<T> getAll();
    T getById(long id);
    T create(T object);
    T update(T object);
    void deleteById(long id);
    void deleteAll();
}
