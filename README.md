# java-filmorate
Filmorate — social network with rating of films based on likes from users. 
Users can add each other to friendlist.

Приложение Failmorate является сервисом с рекомендательной системой, позволяющей выбрать фильм или сериал для просмотра. Приложение хранит информацию о пользователях и фильмах. Есть возможность добавлять оценки и комментарии пользователей фильмам, добавлять пользователей в друзья (с возможностью последующего удаления), добавлять "лайки" комментариям, выводить топ-n фильмов по рейтингу, выводить список рекомендуемых "общих" с другом фильмов с сортировкой по популярности. В приложении реализована рекомендательная система поиска фильмов по пользователям с максимальным "пересечением лайков". Для фильмов реализована возможность добавлять режиссеров с последующим поиском фильмов по режиссеру.
При разработке применены фреймворки Spring Boot, Sprting Data, библиотеки Lombock, Spring Jpa и Spring jdbc. Использована БД H2(для тестов).

# ER-diagram 
<img width="398" alt="image" src="https://user-images.githubusercontent.com/100466117/200110756-a59e822a-7cc3-42e0-b873-e9886ced88bd.png">

# SQL requests examples

Get all films
```
SELECT *
FROM films;
```

Get all users
```
SELECT *
FROM users;
```

Get top-10 popular films
```
SELECT f.name,  
      COUNT(l.user_id) AS film_likes
FROM likes AS l
JOIN films AS f
      ON l.film_id = f.film_id 
GROUP BY f.name
ORDER BY film_likes DESC 
LIMIT 10;
```

Get common friends
```
SELECT u.name
FROM friends AS fr
WHERE u.name = 'user1'
	      OR u.name = 'user2'
	      AND fs.status = 'confirmed'
JOIN users AS u
      ON fr.request_id = u.user_id
JOIN friendship_status AS fs
      ON fr.status_id = fs.status_id
GROUP BY fr.response_id
HAVING COUNT(fr.response_id) > 1;
```

