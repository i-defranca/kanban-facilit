CREATE TABLE department
(
    id         UUID PRIMARY KEY,
    slug       VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

ALTER TABLE department
    ADD CONSTRAINT uniq_department_slug UNIQUE (slug);

CREATE INDEX idx_department_slug ON department (slug);

CREATE TABLE member
(
    id            UUID PRIMARY KEY,
    slug          VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    role          VARCHAR(255),
    email         VARCHAR(255) NOT NULL,
    department_id UUID,
    created_at    TIMESTAMP WITH TIME ZONE,
    updated_at    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_member_department FOREIGN KEY (department_id)
        REFERENCES department (id)
);

ALTER TABLE member
    ADD CONSTRAINT uniq_member_slug UNIQUE (slug);

ALTER TABLE member
    ADD CONSTRAINT uniq_member_email UNIQUE (email);

CREATE INDEX idx_member_slug ON member (slug);
CREATE INDEX idx_member_email ON member (email);

CREATE TABLE project
(
    id                        UUID PRIMARY KEY,
    slug                      VARCHAR(255) NOT NULL,
    name                      VARCHAR(255) NOT NULL,
    delay_days                INTEGER,
    expected_start            DATE,
    expected_end              DATE,
    actual_start              DATE,
    actual_end                DATE,
    remaining_time_percentage NUMERIC(10, 2),
    status                    VARCHAR(50),
    created_at                TIMESTAMP WITH TIME ZONE,
    updated_at                TIMESTAMP WITH TIME ZONE
);

ALTER TABLE project
    ADD CONSTRAINT uniq_project_slug UNIQUE (slug);

CREATE INDEX idx_project_slug ON project (slug);
CREATE INDEX idx_project_status ON project (status);

CREATE TABLE project_members
(
    project_id UUID NOT NULL,
    member_id  UUID NOT NULL,
    PRIMARY KEY (project_id, member_id),
    CONSTRAINT fk_project_member_project FOREIGN KEY (project_id)
        REFERENCES project (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_member_member FOREIGN KEY (member_id)
        REFERENCES member (id) ON DELETE CASCADE
);

CREATE INDEX idx_project_members_project_id ON project_members (project_id);
CREATE INDEX idx_project_members_member_id ON project_members (member_id);
