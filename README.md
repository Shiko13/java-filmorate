# java-filmorate
Filmorate — social network with rating of films based on likes from users. 
Users can add each other to friendlist.

# ER-diagram ([link](https://miro.com/app/board/uXjVPIgCEC0=/) on Miro)
![Filmorate](https://user-images.githubusercontent.com/100466117/198703173-ad321ce8-9884-4961-a54e-c1e03181fda3.jpg)

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
