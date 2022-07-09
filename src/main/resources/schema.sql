drop table if exists mpa, film_genre_coupling, genre_names, marks
    , friends, friendship_status, films, feeds
    , review_scope
    , reviews, users, director_names, film_director_coupling;

create table if not exists Mpa
(
    id   int primary key,
    name varchar(10)
);

create table if not exists genre_names
(
    genre_id   int primary key,
    genre_name varchar(30)
);

create table if not exists friendship_status
(
    status_id   int primary key,
    status_name varchar(30)
);

create table if not exists users
(
    user_id  int generated by default as identity primary key,
    email    varchar(50) unique not null,
    login    varchar(30)        not null,
    name     varchar(30)        not null,
    birthday date               not null
);

create table if not exists films
(
    film_id      int generated by default as identity primary key,
    name         varchar(50)  not null,
    description  varchar(200) not null,
    release_date date         not null,
    duration     int CHECK (duration > 1),
    mpa_id       int REFERENCES Mpa (id)
);

create table if not exists friends
(
    friendship_id        int generated by default as identity primary key,
    user_id              int REFERENCES users (user_id) on delete cascade,
    friend_id            int REFERENCES users (user_id) on delete cascade,
    friendship_status_id int REFERENCES friendship_status (status_id)
);

create table if not exists marks
(
    mark_id        int generated by default as identity primary key,
    film_id        int REFERENCES films (film_id) on delete cascade,
    mark_from_user int REFERENCES users (user_id) on delete cascade,
    mark           int CHECK (mark > 0) AND (mark <= 10),
    CONSTRAINT one_mark_per_user
        UNIQUE (film_id, mark_from_user)
);

create table if not exists film_genre_coupling
(
    coupling_id int generated by default as identity primary key,
    film_id     int REFERENCES films (film_id) on delete cascade,
    genre_id    int REFERENCES genre_names (genre_id)
);

create table if not exists reviews
(
    review_id   INT auto_increment,
    CONTENT     VARCHAR,
    IS_POSITIVE boolean,
    user_id     INT,
    film_id     INT,
    USEFUL      INT,
    constraint REVIEW_PK
        primary key (review_id),
    constraint USER_REVIEW_ID
        foreign key (user_id) references users (user_id),
    constraint FILM_REVIEW_ID
        foreign key (film_id) references films (film_id)
);

create table if not exists review_scope
(
    review_id INT,
    user_id   INT,
    scope     BOOLEAN,
    constraint REVIEW_LIKES_PK
        primary key (review_id, user_id),
    constraint REVIEW_LIKES_USERS_USER_ID_FK
        foreign key (review_id) references reviews (review_id),
    constraint USER_LIKES_DISLIKES_ID_FK
        foreign key (user_id) references users (user_id)
);


create table if not exists director_names
(
    director_id   int generated by default as identity primary key,
    director_name varchar(30)
);

create table if not exists film_director_coupling
(
    coupling_id int generated by default as identity primary key,
    film_id     int REFERENCES films (film_id) on delete cascade,
    director_id int REFERENCES director_names (director_id) on delete cascade
);

create table if not exists feeds
(
    eventId   int generated by default as identity primary key,
    userId    int REFERENCES users (user_id) on delete cascade NOT NULL,
    timestamp long,
    eventType varchar(10),
    operation varchar(10),
    entityId  int
);

