ALTER TABLE POLICY
    ADD COLUMN DATASET_TITLE text;

CREATE TABLE BEHANDLINGSGRUNNLAG_DISTRIBUTION
(
    PURPOSE TEXT PRIMARY KEY,
    STATUS  TEXT NOT NULL
)