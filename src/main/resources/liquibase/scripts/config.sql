--liquibase formatted sql

--changeset volkov_vm:create_table_subscriber
create table subscriber(
    chat_id bigint primary key,
    name varchar(255),
    user_name varchar(255),
    admin boolean default false
);

--changeset volkov_vm:create_table_violation
create table violation(
    id bigserial primary key,
    text text,
    place text,
    chat_id bigint not null,
    foreign key (chat_id) references subscriber(chat_id)
);


--changeset volkov_vm:create_table_photo
create table photo(
    id bigserial primary key,
    data bytea,
    violation_id bigint not null,
    foreign key (violation_id) references violation(id)
);