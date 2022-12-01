package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository("userDb")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from users";

        Set<User> usersSet = jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToSet);
        return new ArrayList<>(usersSet);
    }

    @Override
    public User findById(long userId) {
        String sqlQuery = "select * from users where user_id = ?";

        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, UserDbStorage::mapRow, userId);
        } catch (Exception e) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }

        return user;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "insert into users (email, login, user_name, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return user;
    }

    @Override
    public User update(User user) throws NotFoundException {
        String sqlQuery = "update users set email = ?, login = ?, user_name = ?, birthday = ? where USER_ID = ?";

        findById(user.getId());

        int numRow = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        if (numRow == 0) {
            throw new NotFoundException("User with those parameters not allow updated");
        }

        return user;
    }

    @Override
    public void deleteAll() {
        String sqlQuery = "delete from users";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void deleteById(long userId) {
        String sqlQuery = "delete from users where user_id = ?";
        int numRow = jdbcTemplate.update(sqlQuery, userId);
        if (numRow == 0) {
            throw new NotFoundException(String.format("Film with id = %d not found", userId));
        }
    }

    public static User mapRow(ResultSet resultSet, long rowNum) {
        User user;
        try {
            user = User.builder().
                    id(resultSet.getInt("user_id")).
                    email(resultSet.getString("email")).
                    login(resultSet.getString("login")).
                    name(resultSet.getString("user_name")).
                    birthday(Objects.requireNonNull(resultSet.getDate("birthday")).toLocalDate()).
                    build();
        } catch (Exception e) {
            throw new NotFoundException(" ");
        }
        return user;
    }

    public static Set<User> mapRowToSet(ResultSet resultSet) throws SQLException {
        Set<User> users = new HashSet<>();

        while (resultSet.next()) {
            User user = mapRow(resultSet, resultSet.getInt("user_id"));
            users.add(user);
        }

        return users;
    }
}