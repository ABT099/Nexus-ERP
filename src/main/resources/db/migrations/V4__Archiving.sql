ALTER TABLE event
    ADD COLUMN archived BOOLEAN NOT NULL;

ALTER TABLE expense
    ADD COLUMN archived BOOLEAN NOT NULL;

ALTER TABLE income
    ADD COLUMN archived BOOLEAN NOT NULL;

ALTER TABLE file
    ADD COLUMN archived BOOLEAN NOT NULL;


ALTER TABLE file
    ADD COLUMN tenant_id UUID NOT NULL;

ALTER TABLE file
    ADD CONSTRAINT FK_FILE_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id);

ALTER TABLE project
    ADD COLUMN archived BOOLEAN NOT NULL;

ALTER TABLE project_step
    ADD COLUMN archived BOOLEAN NOT NULL;