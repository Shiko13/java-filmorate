package ru.yandex.practicum.filmorate.storage.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikeMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Like.builder()
                .entityId(rs.getLong("review_id"))
                .userId(rs.getLong("user_id"))
                .isPositive(rs.getBoolean("is_positive"))
                .build();
    }
}
