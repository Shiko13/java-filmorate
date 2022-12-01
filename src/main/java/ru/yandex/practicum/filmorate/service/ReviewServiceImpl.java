package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewServiceImpl implements ReviewService{

    @Override
    public Review create(Review review) {
        return null;
    }

    @Override
    public Review update(Review review) {
        return null;
    }

    @Override
    public void remove(long reviewID) {

    }

    @Override
    public Review getByID(long reviewID) {
        return null;
    }

    @Override
    public List<Review> getAllByFilmID(long reviewID) {
        return null;
    }

    @Override
    public void addLike(long reviewID, long userID) {

    }

    @Override
    public void addDislike(long reviewID, long userID) {

    }

    @Override
    public void removeLike(long reviewID, long userID) {

    }

    @Override
    public void removeDislike(long reviewID, long userID) {

    }
}
