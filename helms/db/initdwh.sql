create table if not exists dwh (
    id bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    username varchar(250) not null,
    numberVehicle varchar(20) not null,
    place varchar(250) not null,
    start timestamp,
    stop timestamp,
    money float,
    requestKey varchar(250) not null
);