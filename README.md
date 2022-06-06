# java-filmorate
Template repository for Filmorate project.

You can see [db ER-diagram](https://github.com/Keindel/java-filmorate/blob/0ac815afa76e4b551ffa5aa7c34d22c2992e7cb9/filmorate%20ER%20diagram.png)
DB request examples:

- get all films
```
SELECT *
FROM films
```

- get all users
```
SELECT *
FROM users
```

- get top-10 popular films
```
SELECT f.name,
  COUNT(l.likes_from_user) AS likes_num
FROM films AS f
LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id
GROUP BY likes_num
ORDER BY DESC
LIMIT 10
```

- get all approved friends for user ID1
```
SELECT f.friend_id AS fid
FROM friends AS f
WHERE user_id = ID1
  AND f.friendship_status_id = 2
```

- get common friends list for users ID1 and ID2
```
SELECT friends_list2.fid
FROM (SELECT f.friend_id AS fid
      FROM friends AS f
      WHERE user_id = ID1
          AND f.friendship_status_id = 2) AS friends_list1
JOIN (SELECT f.friend_id AS fid
      FROM friends AS f
      WHERE user_id = ID2
          AND f.friendship_status_id = 2) AS friends_list2 ON friends_list1.fid = friends_list2.fid
```
