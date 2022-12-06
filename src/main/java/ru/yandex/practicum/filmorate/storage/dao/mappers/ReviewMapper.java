package ru.yandex.practicum.filmorate.storage.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getLong("review_id"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .content(rs.getString("content"))
                .useful(rs.getLong("useful"))
                .build();
    }
}
