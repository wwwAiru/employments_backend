create schema employments;
create table employments.projects
(
    id           bigserial
        primary key,
    project_name varchar(255)
);

alter table employments.projects
    owner to postgres;

create table employments.employments
(
    id               bigserial
        primary key,
    date             timestamp,
    egar_id          varchar(64),
    registered_hours numeric(4, 2),
    project_id       bigint
        constraint fkqi2f6ugls8xp9yee60r95mpb7
            references employments.projects
);

alter table employments.employments
    owner to postgres;

create table employments.weekends
(
    id       bigserial
        primary key,
    date     timestamp,
    day_type varchar(16)
);

alter table employments.weekends
    owner to postgres;