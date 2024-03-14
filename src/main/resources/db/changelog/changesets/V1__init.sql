create table if not exists users
    (
        id varchar(36) primary key,
        username varchar(36) not null,
        password varchar(256) not null,
        constraint users_username_unique unique (username)
    );

create table if not exists files
    (
        id varchar(36) primary key,
        server_name varchar(36) not null,
        original_name varchar(36) not null,
        file_type varchar(36) not null,
        date_time timestamp not null,
        constraint files_server_name_unique unique (server_name)
    );

create table if not exists users_files
    (
        user_id varchar(36) not null,
        file_id varchar(36) not null,
        constraint users_files_users foreign key (user_id) references users(id),
        constraint users_files_files foreign key (file_id) references files(id)
    );

create table if not exists users_roles
    (
        user_id varchar(36) not null,
        role varchar(36) not null,
        primary key (user_id, role),
        constraint users_roles_users foreign key (user_id) references users(id)
    );

