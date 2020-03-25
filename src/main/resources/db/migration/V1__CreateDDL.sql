create table account
(
    id serial primary key,
    name varchar(50) not null,
    type integer not null,
    amount numeric(19,2) not null,
    credit_limit numeric(19,2)
);

create table allocation_center
(
    id serial primary key,
    name varchar(50) not null,
    amount numeric(19,2) not null,
    goal numeric(19,2) not null,
    account_id integer not null references account(id),
    paid_from_id integer references account(id)
);

create table transactions
(
    id serial primary key,
    date_created date not null,
    transaction_type integer not null,
    source_allocation_center_id integer references allocation_center(id),
    destination_allocation_center_id integer references allocation_center(id),
    amount numeric(19,2) not null,
    date_bank_processed date,
    credit_account_id integer references account(id)
);