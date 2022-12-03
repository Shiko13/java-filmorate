package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void remove(long reviewID);

    Review getByID(long reviewID);

    List<Review> getAllByFilmID(long filmID, int count);

    List<Review> getAll(int count);

}
