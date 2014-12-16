CREATE SEQUENCE card_seq 
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
NO CYCLE
NO CACHE;

create table CREDIT_CARD (
	id integer, 
	name varchar2(255) not null unique, 
	user_account_id integer not null, 
	primary key (id),
	foreign key (user_account_id) references USER_ACCOUNT(id)
);

create unique index fk_card_user_account_idx on CREDIT_CARD(user_account_id);
create index card_name_idx on CREDIT_CARD(name);
