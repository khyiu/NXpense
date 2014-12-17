CREATE SEQUENCE expense_seq 
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
NO CYCLE
NO CACHE;

create table EXPENSE (
	id integer not null, 
	expense_type varchar(31) not null, 
	amount decimal(10,2) not null, 
	date date not null, 
	description varchar2(512), 
	verified boolean, 
	user_account_id integer not null,
	card_id integer, 
	primary key (id),
	foreign key (user_account_id) references USER_ACCOUNT(id),
	foreign key (card_id) references CREDIT_CARD(id)
);

create index fk_expense_user_account_idx on EXPENSE(user_account_id);
create index fk_expense_card_idx on EXPENSE(card_id);