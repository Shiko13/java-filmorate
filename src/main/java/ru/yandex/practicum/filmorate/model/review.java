package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Builder
@Getter
public class review {
    private final Integer id;
    @Positive
    private final Integer film_id; // Фильм
    @Positive
    private final Integer user_id; // Пользователь
    private final boolean isPositive; // Тип отзыва позитивный - true или негативный - false
    @NotBlank
    private final String content; // содержание отзыва
    @Builder.Default
    private final long useful = 0; // рейтинг полезности

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        review review = (review) o;
        return isPositive == review.isPositive && useful == review.useful && Objects.equals(id, review.id) && Objects.equals(film_id, review.film_id) && Objects.equals(user_id, review.user_id) && Objects.equals(content, review.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, film_id, user_id, isPositive, content, useful);
    }
}
