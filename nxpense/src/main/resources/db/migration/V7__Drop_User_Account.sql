-- dropping USER_ACCOUNT table
drop sequence account_seq;
drop table USER_ACCOUNT;

-- dropping and recreating USR table
drop table USR;

create table USR (
  id integer not null,
  email varchar2(255) not null unique,
  password varchar2(255) not null,
  remaining_logon_attempt integer default 3,
  temporaryPassword varchar2(255),
  primary key (id),
);

create unique index email_idx on USR(email);

-- dropping and recreating TAG table
drop table TAG;

create table TAG (
  id integer not null,
  name varchar2(255) not null,
  parent_tag_id integer,
  user_id integer not null,
  primary key (id),
  foreign key (user_id) references USR(id)
);

create index fk_tag_user_idx on TAG(user_id);
create index tag_name_idx on TAG(name);

-- dropping and recreating CREDIT_CARD table
drop table CREDIT_CARD;

create table CREDIT_CARD (
  id integer,
  name varchar2(255) not null unique,
  user_id integer not null,
  primary key (id),
  foreign key (user_id) references USR(id)
);

create index fk_card_user__idx on CREDIT_CARD(user_id);
create index card_name_idx on CREDIT_CARD(name);

-- dropping and recreating EXPENSE table
drop table EXPENSE;

create table EXPENSE (
  id integer not null,
  expense_type varchar(31) not null,
  amount decimal(10,2) not null,
  date date not null,
  description varchar2(512),
  verified boolean,
  user_id integer not null,
  card_id integer,
  position integer,
  primary key (id),
  foreign key (user_id) references USR(id),
  foreign key (card_id) references CREDIT_CARD(id)
);

create index fk_expense_user_idx on EXPENSE(user_id);
create index fk_expense_card_idx on EXPENSE(card_id);
