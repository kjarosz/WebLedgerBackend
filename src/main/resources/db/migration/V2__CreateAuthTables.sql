create table users (
    username varchar(50) primary key,
    password varchar(60) not null,
    name varchar(32),
    enabled boolean not null
);

create table authorities (
    username varchar(50) not null references users(username),
    authority varchar(50)
);

create table webledger_session (
    session_id uuid primary key,
    username varchar(50) not null references users(username),
    expires Timestamp not null
);

alter table account add column ownerUsername varchar(50) references users(username) on delete cascade;
alter table allocation_center add column ownerUsername varchar(50) references users(username) on delete cascade;
alter table transactions add column ownerUsername varchar(50) references users(username) on delete cascade;
