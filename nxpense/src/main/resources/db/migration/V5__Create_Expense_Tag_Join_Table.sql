create table EXPENSE_TAG (
	expense_id integer not null,
	tag_id integer not null,
	primary key (expense_id, tag_id),
	foreign key (expense_id) references EXPENSE(id),
	foreign key (tag_id) references TAG(id)
);

create index fk_expense_tag_expense_idx on EXPENSE_TAG(expense_id);
create index fk_expense_tag_tag_idx on EXPENSE_TAG(tag_id);