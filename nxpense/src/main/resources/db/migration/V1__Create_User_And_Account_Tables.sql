CREATE SEQUENCE user_seq 
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
NO CYCLE
NO CACHE;

CREATE SEQUENCE account_seq 
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
NO CYCLE
NO CACHE;

create table USER_ACCOUNT (
	id integer not null, 
	verified_capital decimal(10,2) not null, 
	verifiedDate date not null, 
	primary key (id)
);

create table USR (
	id integer not null,
	email varchar2(255) not null unique, 
	password varchar2(255) not null, 
	remaining_logon_attempt integer default 3, 
	temporaryPassword varchar2(255), 
	username varchar2(255) not null unique,
	account_id integer not null unique,
	primary key (id),
	foreign key (account_id) references USER_ACCOUNT(id)
);

create unique index username_idx on USR(username);
create unique index email_idx on USR(email);
create unique index fk_usr_user_account_idx on USR(account_id);