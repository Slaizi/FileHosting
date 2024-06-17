insert into users (id, username, password)
values ('ddb54c6e-28c9-41e0-b5cb-67cbeee9ec65', 'test', '$2a$08$Un6Aq4SZoyin03X934sPYeNm2VVc2LgBhhToLyIWb5YJNMbgDQH6O'),
('3549e500-6c65-4982-acd0-4420392aa3a7', 'test1', '$2a$08$MFdkYk3ahC/W.qgURckjY.OwdY3utI3KQGYQEJk3/svCH0RpdobbC');

insert into users_roles (user_id, role)
values ('ddb54c6e-28c9-41e0-b5cb-67cbeee9ec65', 'ROLE_USER'),
       ('ddb54c6e-28c9-41e0-b5cb-67cbeee9ec65', 'ROLE_ADMIN'),
       ('3549e500-6c65-4982-acd0-4420392aa3a7', 'ROLE_USER');
