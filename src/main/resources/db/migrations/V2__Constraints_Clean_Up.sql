ALTER TABLE company
    ALTER COLUMN company_name SET NOT NULL;

ALTER TABLE file
    ALTER COLUMN description SET NOT NULL;

ALTER TABLE employee
    ALTER COLUMN employee_code SET NOT NULL;

ALTER TABLE admins_events
    DROP COLUMN event_id;

ALTER TABLE event
    DROP COLUMN id;

ALTER TABLE event
    ADD id BIGINT NOT NULL PRIMARY KEY;

ALTER TABLE admins_events
    ADD event_id BIGINT NOT NULL PRIMARY KEY;

ALTER TABLE admins_events
    ADD CONSTRAINT fk_admeve_on_event FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE expense
    DROP COLUMN expense_category_id;

ALTER TABLE expense_category
    DROP COLUMN id;

ALTER TABLE expense_category
    ADD id BIGINT NOT NULL PRIMARY KEY;

ALTER TABLE expense
    DROP COLUMN id;

ALTER TABLE expense
    ADD id BIGINT NOT NULL PRIMARY KEY;

ALTER TABLE expense
    ADD expense_category_id BIGINT NOT NULL;

ALTER TABLE expense
    ADD CONSTRAINT FK_EXPENSE_ON_EXPENSECATEGORY FOREIGN KEY (expense_category_id) REFERENCES expense_category (id);


ALTER TABLE income
    DROP COLUMN id;

ALTER TABLE income
    ADD id BIGINT NOT NULL PRIMARY KEY;

ALTER TABLE file
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE project
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE project_step
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE message
    ALTER COLUMN text SET NOT NULL;

ALTER TABLE file
    ALTER COLUMN type SET NOT NULL;

ALTER TABLE file
    ALTER COLUMN url SET NOT NULL;