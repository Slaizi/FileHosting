create table if not exists users
    (
        id varchar(36) primary key,
        username varchar(36) not null,
        password varchar(36) not null,
        constraint users_username_unique unique (username)
    );

