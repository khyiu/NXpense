update expense e
set e.position = (
  select count(e2.id) + 1
  from expense e2
  where e2.id < e.id
);

alter table expense alter column position set not null;