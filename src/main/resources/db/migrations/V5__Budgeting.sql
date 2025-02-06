
CREATE SEQUENCE IF NOT EXISTS budget_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE budget
(
    id                  BIGINT          NOT NULL,
    tenant_id           UUID NOT NULL ,
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    archived           BOOLEAN          NOT NULL,
    name text not null,
    start_date timestamp WITHOUT TIME ZONE not null,
    end_date timestamp WITHOUT TIME ZONE not null,
    budget double precision not null,
    current_total double precision null,
    total_income double precision null,
    total_expense double precision null,
    notice text null,
    active boolean not null,
    CONSTRAINT PK_BUDGET PRIMARY KEY (id),
    CONSTRAINT FK_BUDGET_TENANT FOREIGN KEY (tenant_id) REFERENCES tenant (id)
);

ALTER TABLE expense ADD COLUMN budget_id BIGINT;

ALTER TABLE income ADD COLUMN budget_id BIGINT;
