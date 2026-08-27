drop table if exists mapped_people_mapstruct;
drop table if exists mapped_people_lombok_mapstruct;
drop table if exists mapped_people_lombok_modelmapper;
drop table if exists mapped_people_modelmapper;
drop table if exists source_people;

create table source_people (
    id bigint primary key,
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    age integer not null,
    email varchar(160) not null,
    city varchar(80) not null,
    street varchar(160) not null,
    postal_code varchar(20) not null,
    loyalty_points integer not null,
    created_at timestamp not null
);

create table mapped_people_modelmapper (
    id bigint primary key,
    source_id bigint not null,
    full_name varchar(180) not null,
    age integer not null,
    age_group varchar(40) not null,
    email varchar(160) not null,
    address_line varchar(260) not null,
    loyalty_points integer not null,
    created_at timestamp not null
);

create table mapped_people_mapstruct (
    id bigint primary key,
    source_id bigint not null,
    full_name varchar(180) not null,
    age integer not null,
    age_group varchar(40) not null,
    email varchar(160) not null,
    address_line varchar(260) not null,
    loyalty_points integer not null,
    created_at timestamp not null
);

create table mapped_people_lombok_modelmapper (
    id bigint primary key,
    source_id bigint not null,
    full_name varchar(180) not null,
    age integer not null,
    age_group varchar(40) not null,
    email varchar(160) not null,
    address_line varchar(260) not null,
    loyalty_points integer not null,
    created_at timestamp not null
);

create table mapped_people_lombok_mapstruct (
    id bigint primary key,
    source_id bigint not null,
    full_name varchar(180) not null,
    age integer not null,
    age_group varchar(40) not null,
    email varchar(160) not null,
    address_line varchar(260) not null,
    loyalty_points integer not null,
    created_at timestamp not null
);
