CREATE TABLE LEGAL_BASIS (
    legal_basis_id SERIAL PRIMARY KEY,
    description VARCHAR(500) NOT NULL
);

CREATE TABLE PURPOSE (
    purpose_id VARCHAR(10) PRIMARY KEY,
    description VARCHAR(500) NOT NULL
);

CREATE TABLE POLICY (
    policy_id SERIAL PRIMARY KEY,
    information_type_id INTEGER NOT NULL,
    purpose_id VARCHAR(10) NOT NULL,
    legal_basis_id INTEGER NOT NULL,
    CONSTRAINT FK_POLICY_PURPOSE FOREIGN KEY (purpose_id)
        REFERENCES PURPOSE (purpose_id),
    CONSTRAINT FK_POLICY_LEGAL_BASIS FOREIGN KEY (legal_basis_id)
        REFERENCES LEGAL_BASIS (legal_basis_id)
);

