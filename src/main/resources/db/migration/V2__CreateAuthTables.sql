create table user (
    username varchar(32) primary key,
    password bytea not null,
    name varchar(32)
);

alter table account add column ownerUsername;
alter table account add constraint accountOwner foreign key (ownerUsername) references user(username);

alter table allocation_center add column ownerUsername;
alter table allocation_center add constraint allocation_center_owner foreign key (ownerUsername) references user(username);

alter table transactions add column ownerUsername;
alter table transactions add constraint transactions_owner foreign key (ownerUsername) references user(username);
