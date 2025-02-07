ALTER TABLE interaction ADD COLUMN project_id INTEGER;

ALTER TABLE interaction ADD COLUMN step_id INTEGER;

ALTER TABLE interaction ADD CONSTRAINT fk_interaction_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE interaction ADD CONSTRAINT fk_interaction_step FOREIGN KEY (step_id) REFERENCES project_step(id);
