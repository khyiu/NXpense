drop table CREDIT_CARD;
drop SEQUENCE card_seq;

drop table EXPENSE;

create table EXPENSE (
  id integer not null,
  expense_type varchar(31) not null,
  amount decimal(10,2) not null,
  date date not null,
  description varchar2(512),
  verified boolean,
  user_id integer not null,
  position integer,
  primary key (id),
  foreign key (user_id) references USR(id),
);

create index fk_expense_user_idx on EXPENSE(user_id);