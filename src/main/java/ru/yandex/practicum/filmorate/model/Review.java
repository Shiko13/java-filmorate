package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Builder
@Getter
public class Review {
    private final Integer id;
    @Positive
    private final Integer filmID; // Фильм
    @Positive
    private final Integer userID; // Пользователь
    private final boolean isPositive; // Тип отзыва позитивный - true или негативный - false
    @NotBlank
    private final String content; // содержание отзыва
    @Builder.Default
    private final long useful = 0; // рейтинг полезности

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return isPositive == review.isPositive && useful == review.useful && Objects.equals(id, review.id) && Objects.equals(filmID, review.filmID) && Objects.equals(userID, review.userID) && Objects.equals(content, review.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filmID, userID, isPositive, content, useful);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", filmID=" + filmID +
                ", userID=" + userID +
                ", isPositive=" + isPositive +
                ", content='" + content + '\'' +
                ", useful=" + useful +
                '}';
    }
}
