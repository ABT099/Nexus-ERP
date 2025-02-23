alter table budget add column project_id integer;

alter table budget add constraint fk_budget_project foreign key (project_id) references project (id);

alter table income alter column amount type bigint;

alter table income add column charge_id text null;
alter table income add column source text null;

alter table expense alter column amount type bigint;