package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserEventListStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserEventListStorage userEventListStorage;

    @Override
    public List<User> readAll(long userId) {
        String sqlQuery = "select * from FRIENDSHIP as f " +
                "join users as u on f.user2_id = u.user_id " +
                "where f.USER1_ID = ?";

        List<User> users = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        while (sqlRowSet.next()) {
            User user = mapRow(sqlRowSet);
            users.add(user);
        }

        return users;
    }

    @Override
    public Friendship create(long userOneId, long userTwoId) {
        String sqlQuery = "insert into friendship values (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userOneId, userTwoId);
        } catch (Exception e) {
            throw new NotFoundException(
                    String.format("Пользователь c ID № %s или %s не существует", userOneId, userTwoId)
            );
        }

        int numRow2 = userEventListStorage.addEvent(userOneId,"FRIEND", "ADD", userTwoId);
        if(numRow2==0){
            throw new NotFoundException(String.format("User with id = %d not found", userOneId));
        }

        return Friendship.builder().
                userOneId(userOneId).
                userTwoId(userTwoId).
                build();
    }

    @Override
    public void delete(long userOneId, long userTwoId) {
        String sqlQuery = "delete from FRIENDSHIP where USER1_ID = ? and USER2_ID = ?";

        int numRow = jdbcTemplate.update(sqlQuery, userOneId, userTwoId);
        if (numRow == 0) {
            throw new NotFoundException(String.format("User with id = %d or user with id = %d not found",userOneId, userTwoId));
        }

        int numRow2 = userEventListStorage.addEvent(userOneId,"FRIEND", "REMOVE", userTwoId);
        if(numRow2==0){
            throw new NotFoundException(String.format("User with id = %d not found", userOneId));
        }
    }

    @Override
    public List<User> readCommon(long userOneId, long userTwoId) {
        String sqlQuery = "select * from users as u " +
                "join friendship f1 on u.user_id = f1.user2_id " +
                "join friendship f2 on u.user_id = f2.user2_id " +
                "where f1.user1_id = ? and f2.user1_id = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userOneId, userTwoId);
        List<User> users = new ArrayList<>();
        while (sqlRowSet.next()) {
            User user = mapRow(sqlRowSet);
            users.add(user);
        }

        return users;
    }

    public static User mapRow(SqlRowSet sqlRowSet) {
        return User.builder().
                id(sqlRowSet.getInt("user_id")).
                email(sqlRowSet.getString("email")).
                login(sqlRowSet.getString("login")).
                name(sqlRowSet.getString("user_name")).
                birthday(Objects.requireNonNull(sqlRowSet.getDate("birthday")).toLocalDate()).
                build();
    }
}