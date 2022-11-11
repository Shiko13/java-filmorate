package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> readAllFriends(long userId) {
        String sqlQuery = "select * from FRIENDSHIP as f " +
                "join users as u on f.user2_id = u.user_id " +
                "where f.USER1_ID = ?";

        List<User> users = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        while (sqlRowSet.next()) {
            User user = mapRowToUser(sqlRowSet);
            users.add(user);
        }

        return users;
    }

    @Override
    public Friendship createFriend(long userOneId, long userTwoId) {
        String sqlQuery = "insert into friendship values (?, ?)";
        jdbcTemplate.update(sqlQuery, userOneId, userTwoId);
        return Friendship.builder().
                userOneId(userOneId).
                userTwoId(userTwoId).
                build();
    }

    @Override
    public void deleteFromFriends(long userOneId, long userTwoId) {
        String sqlQuery = "delete from FRIENDSHIP where USER1_ID = ? and USER2_ID = ?";
        jdbcTemplate.update(sqlQuery, userOneId, userTwoId);
    }

    @Override
    public List<User> readCommonFriends(long userOneId, long userTwoId) {
        String sqlQuery = "select * from users as u " +
                "join friendship f1 on u.user_id = f1.user2_id " +
                "join friendship f2 on u.user_id = f2.user2_id " +
                "where f1.user1_id = ? and f2.user1_id = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userOneId, userTwoId);
        List<User> users = new ArrayList<>();
        while (sqlRowSet.next()) {
            User user = mapRowToUser(sqlRowSet);
            users.add(user);
        }

        return users;
    }

    public User mapRowToUser(SqlRowSet sqlRowSet) {
        return User.builder().
                id(sqlRowSet.getInt("user_id")).
                email(sqlRowSet.getString("email")).
                login(sqlRowSet.getString("login")).
                name(sqlRowSet.getString("user_name")).
                birthday(Objects.requireNonNull(sqlRowSet.getDate("birthday")).toLocalDate()).
                build();
    }
}