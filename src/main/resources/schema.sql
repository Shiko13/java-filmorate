DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR CHECK (login <> ''),
    user_name VARCHAR,
    birthday DATE CHECK (birthday <= NOW())
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_rating_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_rating_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
    director_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name VARCHAR(100),
    director_surname VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name VARCHAR NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT CHECK (duration > 0),
    mpa_rating INT REFERENCES mpa_ratings (mpa_rating_id),
    director_id INT REFERENCES directors (director_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT REFERENCES films (FILM_ID) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (USER_ID) ON DELETE CASCADE,
    PRIMARY KEY (user_id,film_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user1_id BIGINT REFERENCES users (USER_ID) ON DELETE CASCADE,
    user2_id BIGINT REFERENCES users (USER_ID) ON DELETE CASCADE,
    PRIMARY KEY (user1_id,user2_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT REFERENCES films (FILM_ID) ON DELETE CASCADE,
    genre_id INT REFERENCES genres (GENRE_ID) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id BIGINT REFERENCES films (FILM_ID) ON DELETE CASCADE,
    director_id INT REFERENCES directors (DIRECTOR_ID) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);