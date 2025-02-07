CREATE SEQUENCE IF NOT EXISTS _user_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS admin_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS chat_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS company_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS customer_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS employee_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS event_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS expense_category_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS expense_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS file_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS interaction_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS message_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS notification_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS income_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS project_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS project_step_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE _user
(
    id         BIGINT       NOT NULL,
    tenant_id  VARCHAR(255) NOT NULL,
    username   TEXT         NOT NULL,
    password   TEXT         NOT NULL,
    user_type  TEXT         NOT NULL,
    avatar_url TEXT,
    archived   BOOLEAN      NOT NULL,
    CONSTRAINT pk__user PRIMARY KEY (id)
);

CREATE TABLE admin
(
    id           BIGINT  NOT NULL,
    first_name   TEXT    NOT NULL,
    last_name    TEXT    NOT NULL,
    user_id      BIGINT  NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE,
    archived     BOOLEAN NOT NULL,
    CONSTRAINT pk_admin PRIMARY KEY (id)
);

CREATE TABLE admins_events
(
    admin_id BIGINT  NOT NULL,
    event_id INTEGER NOT NULL,
    CONSTRAINT pk_admins_events PRIMARY KEY (admin_id, event_id)
);

CREATE TABLE chat
(
    id        BIGINT       NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_chat PRIMARY KEY (id)
);

CREATE TABLE company
(
    id           BIGINT  NOT NULL,
    user_id      BIGINT  NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE,
    archived     BOOLEAN NOT NULL,
    company_name TEXT,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

CREATE TABLE customer
(
    id           BIGINT  NOT NULL,
    first_name   TEXT    NOT NULL,
    last_name    TEXT    NOT NULL,
    user_id      BIGINT  NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE,
    archived     BOOLEAN NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE employee
(
    id            BIGINT  NOT NULL,
    first_name    TEXT    NOT NULL,
    last_name     TEXT    NOT NULL,
    user_id       BIGINT  NOT NULL,
    created_date  TIMESTAMP WITHOUT TIME ZONE,
    archived      BOOLEAN NOT NULL,
    employee_code VARCHAR(255),
    CONSTRAINT pk_employee PRIMARY KEY (id)
);

CREATE TABLE event
(
    id                  INTEGER      NOT NULL,
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    name                TEXT         NOT NULL,
    description         TEXT         NOT NULL,
    type                VARCHAR(255) NOT NULL,
    status              VARCHAR(255) NOT NULL,
    date                TIMESTAMP WITHOUT TIME ZONE,
    urgent              BOOLEAN      NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE TABLE expense
(
    id                  INTEGER          NOT NULL,
    amount              DOUBLE PRECISION NOT NULL,
    payment_date        TIMESTAMP WITHOUT TIME ZONE,
    project_id          INTEGER,
    tenant_id           VARCHAR(255),
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    expense_category_id INTEGER          NOT NULL,
    CONSTRAINT pk_expense PRIMARY KEY (id)
);

CREATE TABLE expense_category
(
    id          INTEGER NOT NULL,
    name        TEXT    NOT NULL,
    description TEXT    NOT NULL,
    CONSTRAINT pk_expensecategory PRIMARY KEY (id)
);

CREATE TABLE file
(
    id                  INTEGER NOT NULL,
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    name                VARCHAR(255),
    description         VARCHAR(255),
    type                VARCHAR(255),
    url                 VARCHAR(255),
    CONSTRAINT pk_file PRIMARY KEY (id)
);

CREATE TABLE interaction
(
    id               BIGINT                      NOT NULL,
    interacted_by_id BIGINT                      NOT NULL,
    title            TEXT                        NOT NULL,
    description      TEXT                        NOT NULL,
    interaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_interaction PRIMARY KEY (id)
);

CREATE TABLE message
(
    id         BIGINT NOT NULL,
    sender_id  BIGINT NOT NULL,
    chat_id    BIGINT NOT NULL,
    text       VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_message PRIMARY KEY (id)
);

CREATE TABLE notification
(
    id      BIGINT  NOT NULL,
    user_id BIGINT  NOT NULL,
    title   TEXT    NOT NULL,
    body    TEXT    NOT NULL,
    date    TIMESTAMP WITHOUT TIME ZONE,
    read    BOOLEAN NOT NULL,
    type    VARCHAR(255),
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

CREATE TABLE income
(
    id                  INTEGER          NOT NULL,
    amount              DOUBLE PRECISION NOT NULL,
    payment_date        TIMESTAMP WITHOUT TIME ZONE,
    project_id          INTEGER,
    tenant_id           VARCHAR(255),
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    payer_id            BIGINT           NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

CREATE TABLE project
(
    id                  INTEGER                     NOT NULL,
    tenant_id           VARCHAR(255),
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    name                TEXT                        NOT NULL,
    description         TEXT                        NOT NULL,
    start_date          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expected_end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    actual_end_date     TIMESTAMP WITHOUT TIME ZONE,
    status              VARCHAR(255),
    owner_id            BIGINT                      NOT NULL,
    price               DOUBLE PRECISION            NOT NULL,
    CONSTRAINT pk_project PRIMARY KEY (id)
);

CREATE TABLE project_employees
(
    employee_id BIGINT  NOT NULL,
    project_id  INTEGER NOT NULL,
    CONSTRAINT pk_project_employees PRIMARY KEY (employee_id, project_id)
);

CREATE TABLE project_files
(
    file_id    INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    CONSTRAINT pk_project_files PRIMARY KEY (file_id, project_id)
);

CREATE TABLE project_step
(
    id                  INTEGER                     NOT NULL,
    created_by_id       BIGINT,
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by_id BIGINT,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    name                TEXT                        NOT NULL,
    description         TEXT                        NOT NULL,
    start_date          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expected_end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    actual_end_date     TIMESTAMP WITHOUT TIME ZONE,
    status              VARCHAR(255),
    project_id          INTEGER                     NOT NULL,
    CONSTRAINT pk_projectstep PRIMARY KEY (id)
);

CREATE TABLE step_employees
(
    employee_id BIGINT  NOT NULL,
    step_id     INTEGER NOT NULL,
    CONSTRAINT pk_step_employees PRIMARY KEY (employee_id, step_id)
);

CREATE TABLE tenant
(
    id           VARCHAR(255) NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_tenant PRIMARY KEY (id)
);

ALTER TABLE _user
    ADD CONSTRAINT uc__user_username UNIQUE (username);

ALTER TABLE admin
    ADD CONSTRAINT uc_admin_user UNIQUE (user_id);

ALTER TABLE company
    ADD CONSTRAINT uc_company_user UNIQUE (user_id);

ALTER TABLE customer
    ADD CONSTRAINT uc_customer_user UNIQUE (user_id);

ALTER TABLE employee
    ADD CONSTRAINT uc_employee_user UNIQUE (user_id);

ALTER TABLE project_interactions
    ADD CONSTRAINT uc_project_interactions_interactions UNIQUE (interactions_id);

ALTER TABLE project_step_interactions
    ADD CONSTRAINT uc_project_step_interactions_interactions UNIQUE (interactions_id);

ALTER TABLE admin
    ADD CONSTRAINT FK_ADMIN_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE company
    ADD CONSTRAINT FK_COMPANY_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE customer
    ADD CONSTRAINT FK_CUSTOMER_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE employee
    ADD CONSTRAINT FK_EMPLOYEE_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE event
    ADD CONSTRAINT FK_EVENT_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES _user (id);

ALTER TABLE event
    ADD CONSTRAINT FK_EVENT_ON_LASTMODIFIEDBY FOREIGN KEY (last_modified_by_id) REFERENCES _user (id);

ALTER TABLE expense
    ADD CONSTRAINT FK_EXPENSE_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES _user (id);

ALTER TABLE expense
    ADD CONSTRAINT FK_EXPENSE_ON_EXPENSECATEGORY FOREIGN KEY (expense_category_id) REFERENCES expense_category (id);

ALTER TABLE expense
    ADD CONSTRAINT FK_EXPENSE_ON_LASTMODIFIEDBY FOREIGN KEY (last_modified_by_id) REFERENCES _user (id);

ALTER TABLE expense
    ADD CONSTRAINT FK_EXPENSE_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES _user (id);

ALTER TABLE file
    ADD CONSTRAINT FK_FILE_ON_LASTMODIFIEDBY FOREIGN KEY (last_modified_by_id) REFERENCES _user (id);

ALTER TABLE interaction
    ADD CONSTRAINT FK_INTERACTION_ON_INTERACTEDBY FOREIGN KEY (interacted_by_id) REFERENCES _user (id);

ALTER TABLE message
    ADD CONSTRAINT FK_MESSAGE_ON_CHAT FOREIGN KEY (chat_id) REFERENCES chat (id);

ALTER TABLE message
    ADD CONSTRAINT FK_MESSAGE_ON_SENDER FOREIGN KEY (sender_id) REFERENCES _user (id);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE income
    ADD CONSTRAINT FK_INCOME_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES _user (id);

ALTER TABLE income
    ADD CONSTRAINT FK_INCOME_ON_LASTMODIFIEDBY FOREIGN KEY (last_modified_by_id) REFERENCES _user (id);

ALTER TABLE income
    ADD CONSTRAINT FK_INCOME_ON_PAYER FOREIGN KEY (payer_id) REFERENCES _user (id);

ALTER TABLE income
    ADD CONSTRAINT FK_INCOME_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE project_step
    ADD CONSTRAINT FK_PROJECTSTEP_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES _user (id);

ALTER TABLE project_step
    ADD CONSTRAINT FK_PROJECTSTEP_ON_LASTMODIFIEDBY FOREIGN KEY (last_modified_by_id) REFERENCES _user (id);

ALTER TABLE project_step
    ADD CONSTRAINT FK_PROJECTSTEP_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE project
    ADD CONSTRAINT FK_PROJECT_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES _user (id);

ALTER TABLE project
    ADD CONSTRAINT FK_PROJECT_ON_LASTMODIFIEDBY FOREIGN KEY (last_modified_by_id) REFERENCES _user (id);

ALTER TABLE project
    ADD CONSTRAINT FK_PROJECT_ON_OWNER FOREIGN KEY (owner_id) REFERENCES _user (id);

ALTER TABLE admins_events
    ADD CONSTRAINT fk_admeve_on_admin FOREIGN KEY (admin_id) REFERENCES admin (id);

ALTER TABLE admins_events
    ADD CONSTRAINT fk_admeve_on_event FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE project_employees
    ADD CONSTRAINT fk_proemp_on_employee FOREIGN KEY (employee_id) REFERENCES employee (id);

ALTER TABLE project_employees
    ADD CONSTRAINT fk_proemp_on_project FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE project_files
    ADD CONSTRAINT fk_profil_on_file FOREIGN KEY (file_id) REFERENCES file (id);

ALTER TABLE project_files
    ADD CONSTRAINT fk_profil_on_project FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE project_interactions
    ADD CONSTRAINT fk_proint_on_interaction FOREIGN KEY (interactions_id) REFERENCES interaction (id);

ALTER TABLE project_interactions
    ADD CONSTRAINT fk_proint_on_project FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE project_step_interactions
    ADD CONSTRAINT fk_prosteint_on_interaction FOREIGN KEY (interactions_id) REFERENCES interaction (id);

ALTER TABLE project_step_interactions
    ADD CONSTRAINT fk_prosteint_on_project_step FOREIGN KEY (project_step_id) REFERENCES project_step (id);

ALTER TABLE step_employees
    ADD CONSTRAINT fk_steemp_on_employee FOREIGN KEY (employee_id) REFERENCES employee (id);

ALTER TABLE step_employees
    ADD CONSTRAINT fk_steemp_on_project_step FOREIGN KEY (step_id) REFERENCES project_step (id);