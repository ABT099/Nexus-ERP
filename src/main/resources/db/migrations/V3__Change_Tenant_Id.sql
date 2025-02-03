ALTER TABLE tenant
    DROP COLUMN id;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE tenant
    ADD id UUID NOT NULL DEFAULT uuid_generate_v4();

ALTER TABLE message
    DROP COLUMN text;

ALTER TABLE message
    ADD message_text TEXT NOT NULL;