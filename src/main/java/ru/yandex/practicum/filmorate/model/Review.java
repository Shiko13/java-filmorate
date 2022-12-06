package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.Getter;
import ru.yandex.practicum.filmorate.model.validators.EntityIdExistValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Builder(toBuilder = true)
@Getter
public class Review {
    @JsonSetter("reviewId")
    private final Long id;
    @EntityIdExistValidation
    private final long userId; // Пользователь
    @EntityIdExistValidation
    private final long filmId; // Фильм
    @NotNull
    private final Boolean isPositive; // Тип отзыва позитивный - true или негативный - false
    @NotBlank
    private final String content; // содержание отзыва
    @Builder.Default
    private final long useful = 0; // рейтинг полезности

    @JsonGetter("reviewId")
    public Long getId() {
        return id;
    }

    @JsonGetter("isPositive")
    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return isPositive == review.isPositive && useful == review.useful
                && Objects.equals(id, review.id) && Objects.equals(filmId, review.filmId)
                && Objects.equals(userId, review.userId) && Objects.equals(content, review.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filmId, userId, isPositive, content, useful);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", filmID=" + filmId +
                ", userId=" + userId +
                ", isPositive=" + isPositive +
                ", content='" + content + '\'' +
                ", useful=" + useful +
                '}';
    }
}
