create table if not exists users (
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    username varchar(250) not null,
    firstName varchar(250) not null,
    lastName varchar(250) not null,
    email varchar(50),
    phone varchar(50)
);

create table if not exists orders (
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    username varchar(250) not null,
    ordername varchar(250) not null,
    quantity bigint not null,
    requestKey varchar(250) not null,
    deleted timestamp
);

create table if not exists ordersversion (
    id bigint GENERATED BY DEFAULT AS IDENTITY,
    username varchar(250) not null,
    version bigint not null
);