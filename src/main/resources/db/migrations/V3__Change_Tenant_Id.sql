ALTER TABLE tenant
    DROP COLUMN id;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE tenant
    ADD id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4();

ALTER TABLE _user
    DROP COLUMN tenant_id;

ALTER TABLE _user
    ADD tenant_id UUID NOT NULL;

ALTER TABLE chat
    DROP COLUMN tenant_id;

ALTER TABLE chat
    ADD tenant_id UUID NOT NULL;

ALTER TABLE payment
    DROP COLUMN tenant_id;

ALTER TABLE payment
    ADD tenant_id UUID NOT NULL;

ALTER TABLE expense
    DROP COLUMN tenant_id;

ALTER TABLE expense
    ADD tenant_id UUID NOT NULL;

ALTER TABLE project
    DROP COLUMN tenant_id;

ALTER TABLE project
    ADD tenant_id UUID NOT NULL;


ALTER TABLE _user
    ADD CONSTRAINT FK_USER_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);

ALTER TABLE chat
    ADD CONSTRAINT FK_CHAT_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);


ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);


ALTER TABLE expense
    ADD CONSTRAINT FK_EXPENSE_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);

ALTER TABLE project
    ADD CONSTRAINT FK_PROJECT_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);



ALTER TABLE message
    DROP COLUMN text;

ALTER TABLE message
    ADD message_text TEXT NOT NULL;