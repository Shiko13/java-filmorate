package ru.yandex.practicum.filmorate.util;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.FilmSearchBy;

public class StringToFilmSearchByConverter implements Converter<String, FilmSearchBy> {

    @Override
    public FilmSearchBy convert(String source) {
        return FilmSearchBy.valueOf(source.toUpperCase());
    }
}
