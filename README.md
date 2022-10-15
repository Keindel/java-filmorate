# _Java-filmorate_
Filmorate project - social network with rating of films based on marks from users. Users can add each other to friendlist.

### _Technologies used_
REST API service with Spring-Boot, Jdbc, H2-database, Java 11, Lombok

### _Additional features implemented during teamwork_
1. _add-reviews_ - users make reviews on films, like or dislike and sort by rating - by [ABoyarov](https://github.com/aboyarov)
2. _add-feed_ - ability to get last events happened on the platform - by https://github.com/kompasvideo
3. _add-director_ - get films by director sorted by year or likes - by https://github.com/Rexsus-bit
4. _add-search_ - search by film title and director name - by https://github.com/Mark33SC
5. _add-recommendations_ - recommendation system based on similar users and number of likes - by myself: [Andrey Maksimov](https://github.com/Keindel)
6. _add-marks_ - refactor **likes** to **marks**
   - change likes to marks in the project - by myself: [Andrey Maksimov](https://github.com/Keindel)
   - actualize rating and popular films listing - by [ABoyarov](https://github.com/aboyarov)
   - make new recommendation algorithm with marks - by myself: [Andrey Maksimov](https://github.com/Keindel)

### _Entity relationship diagram_
![db ER-diagram](./filmorate%20ER%20diagram.png)

### _Starting the service_
CLI start commands:  
`mvn package`  
`cd target`  
`java -jar filmorate-0.0.1-SNAPSHOT.jar`

___
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
