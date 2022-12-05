package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.UserEventListStorage;

import java.time.Instant;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserEventListDbStorage implements UserEventListStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<UserEvent> getListById(long userId) {
        String sqlQuery = "select * from EVENT_LIST as f " +
                "where f.USER_ID = ?";

        List<UserEvent> userEvents = new LinkedList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        while (sqlRowSet.next()) {
            UserEvent userEvent = mapRow(sqlRowSet);
            userEvents.add(userEvent);
        }

        return userEvents;
    }

    @Override
    public int addEvent(long userId, String eventType, String operation, long entityId) {
        String sqlQuery = "insert into event_list(timestamps,user_id,event_type,operation,entity_id )" +
                " values (?, ?, ?, ?,  ?)";
        return  jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli() ,
                userId, eventType, operation, entityId);
    }

    public static UserEvent mapRow(SqlRowSet sqlRowSet) {
        return UserEvent.builder().
                timestamp(sqlRowSet.getLong("timestamps")).
                userId(sqlRowSet.getLong("user_id")).
                eventType(sqlRowSet.getString("event_type")).
                operation(sqlRowSet.getString("operation")).
                eventId(sqlRowSet.getLong("event_id")).
                entityId(sqlRowSet.getLong("entity_id")).
                build();
    }
}
