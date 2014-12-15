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
	user_id integer not null unique, 
	primary key (id),
	foreign key (user_id) references USR(id)
);

create unique index fk_user_account_usr_idx on USER_ACCOUNT(user_id);