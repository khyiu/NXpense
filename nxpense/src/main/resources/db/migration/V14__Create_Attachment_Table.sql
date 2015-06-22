create table EXPENSE_ATTACHMENT (
  expense_id integer,
  filename varchar(60),
  content blob,

  primary key(expense_id, filename),
  foreign key (expense_id) references EXPENSE(id)
);

create index fk_expense_attachment_idx on EXPENSE_ATTACHMENT(expense_id);