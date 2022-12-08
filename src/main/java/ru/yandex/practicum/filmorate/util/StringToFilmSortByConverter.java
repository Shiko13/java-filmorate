package ru.yandex.practicum.filmorate.util;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

public class StringToFilmSortByConverter implements Converter<String, FilmSortBy> {
    @Override
    public FilmSortBy convert(String source) {
        return FilmSortBy.valueOf(source.toUpperCase());
    }
}
