drop table if exists auth;
drop table if exists product;
drop table if exists token;
drop table if exists users;

create table auth
(
    id         bigint not null auto_increment,
    username   varchar(255),
    password   varchar(255),
    status     enum ('ENABLED','LOCKED'),
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    primary key (id)
) engine = InnoDB;

create table product
(
    id             bigint not null auto_increment,
    seller_id      bigint,
    name           varchar(255),
    price          bigint,
    stock_quantity bigint,
    total_quantity bigint,
    status         enum ('OUT_OF_STOCK','RESERVED','SALE'),
    created_at     datetime default current_timestamp,
    updated_at     datetime default current_timestamp on update current_timestamp,
    primary key (id)
) engine = InnoDB;

create table token
(
    id            bigint not null auto_increment,
    auth_id       bigint,
    refresh_token varchar(255),
    created_at    datetime default current_timestamp,
    updated_at    datetime default current_timestamp on update current_timestamp,
    primary key (id)
) engine = InnoDB;

create table users
(
    id         bigint not null auto_increment,
    auth_id    bigint,
    created_at datetime default current_timestamp,
    updated_at datetime default current_timestamp on update current_timestamp,
    primary key (id)
) engine = InnoDB;

