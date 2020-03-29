create table users (
    username varchar(32) primary key,
    password bytea not null,
    name varchar(32)
);

create table webledger_session (
    session_id uuid primary key,
    username varchar(32) not null references users(username),
    expires Timestamp not null
);

alter table account add column ownerUsername varchar(32) references users(username) on delete cascade;
alter table allocation_center add column ownerUsername varchar(32) references users(username) on delete cascade;
alter table transactions add column ownerUsername varchar(32) references users(username) on delete cascade;
