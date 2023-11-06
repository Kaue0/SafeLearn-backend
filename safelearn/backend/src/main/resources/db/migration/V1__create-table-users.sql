create table users(

    id serial not null,
    created_at timestamp,
    updated_at timestamp,
    name varchar(50) not null,
    username varchar(30) not null unique,
    description varchar(255),
    ano varchar(1),
    materia varchar(50),
    phone varchar(20) not null,
    email varchar(70) not null unique,
    password varchar(255) not null,
    photo_link varchar(1000),
    deleted boolean not null default false,

    primary key(id)

);