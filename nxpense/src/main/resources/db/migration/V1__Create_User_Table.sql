CREATE SEQUENCE user_seq 
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
NO CYCLE
NO CACHE;

create table USR (
	id integer not null,
	email varchar2(255) not null unique, 
	password varchar2(255) not null, 
	remaining_logon_attempt integer default 3, 
	temporaryPassword varchar2(255), 
	username varchar2(255) not null unique, 
	primary key (id)
);

create unique index username_idx on USR(username);
create unique index email_idx on USR(email);