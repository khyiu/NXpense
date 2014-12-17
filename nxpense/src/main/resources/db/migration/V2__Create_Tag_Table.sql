CREATE SEQUENCE tag_seq 
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
NO CYCLE
NO CACHE;


create table TAG (
	id integer not null, 
	name varchar2(255) not null, 
	parent_tag_id integer, 
	user_account_id integer not null, 
	primary key (id),
	foreign key (user_account_id) references USER_ACCOUNT(id)
);

create index fk_tag_user_account_idx on TAG(user_account_id);
create index tag_name_idx on TAG(name);