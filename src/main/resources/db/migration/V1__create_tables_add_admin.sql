create table users
(
    id bigserial primary key not null unique,
    username varchar(100) not null unique,
    password varchar(255) not null,
    role varchar(20) not null,
    enabled bool not null default true,
    created_date timestamp default current_timestamp,
    last_modified_date timestamp default current_timestamp,
    created_by varchar(100) default 'admin',
    last_modified_by varchar(100) default 'admin'
);

create table positions
(
    id bigserial primary key not null unique,
    name varchar(100) not null unique,
    created_date timestamp default current_timestamp,
    last_modified_date timestamp default current_timestamp,
    created_by varchar(100) default 'admin',
    last_modified_by varchar(100) default 'admin'
);

create table departments
(
    id bigserial primary key not null unique,
    name varchar(100) not null unique,
    created_date timestamp default current_timestamp,
    last_modified_date timestamp default current_timestamp,
    created_by varchar(100) default 'admin',
    last_modified_by varchar(100) default 'admin'
);

create table employees
(
    id bigserial primary key not null unique,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    birth_date timestamp default current_timestamp,
    employment_date timestamp default current_timestamp,
    position_id bigint not null,
    department_id bigint not null,
    created_date timestamp default current_timestamp,
    last_modified_date timestamp default current_timestamp,
    created_by varchar(100) default 'admin',
    last_modified_by varchar(100) default 'admin'
);

insert into users(username, password, role, enabled,
    created_date, last_modified_date, created_by, last_modified_by)
values('admin', '$2a$12$zf83GVVQV/Kd/I488ZN6yO9lV71zuDW8KNNV/uUa/aVkpWb/nbcNK', 'ADMIN', true,
    current_timestamp, current_timestamp, 'admin', 'admin');