insert into users (id, username, password)
values ('b16558d8-f3bf-4c81-8b87-feea237717ec', 'John', '$2a$08$Nsz8BDJugIpYOH.cmfMF3OqIMBxVmU/v7YoMjvZMCgOJf3xKmEnBW');

insert into users_roles (user_id, role)
values
('b16558d8-f3bf-4c81-8b87-feea237717ec', 'ROLE_USER'),
('b16558d8-f3bf-4c81-8b87-feea237717ec', 'ROLE_ADMIN');
