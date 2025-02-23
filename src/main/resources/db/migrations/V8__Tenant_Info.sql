alter table tenant
    add column name text not null unique;

alter table tenant
    add column email text not null;

alter table tenant
    add column phone_number text not null;

alter table tenant
    add column subscription_status text not null;

alter table tenant
    add column stripe_customer_id text not null;

alter table tenant
    add column stripe_account_id text not null;