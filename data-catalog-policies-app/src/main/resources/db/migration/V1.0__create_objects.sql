CREATE SEQUENCE SEQ_LEGAL_BASIS;

CREATE SEQUENCE SEQ_POLICY;

CREATE SEQUENCE SEQ_PURPOSE;

CREATE TABLE LEGAL_BASIS (
    LEGAL_BASIS_ID INTEGER DEFAULT nextval('SEQ_LEGAL_BASIS') PRIMARY KEY,
    DESCRIPTION VARCHAR(500) NOT NULL
);

CREATE TABLE PURPOSE (
    PURPOSE_ID INTEGER DEFAULT nextval('SEQ_PURPOSE') PRIMARY KEY,
    PURPOSE_CODE VARCHAR(10) CONSTRAINT UK_PURPOSE_CODE UNIQUE,
    DESCRIPTION VARCHAR(500) NOT NULL
);

CREATE TABLE POLICY (
    POLICY_ID INTEGER DEFAULT nextval('SEQ_POLICY') PRIMARY KEY,
    INFORMATION_TYPE_ID INTEGER NOT NULL,
    PURPOSE_ID INTEGER NOT NULL,
    LEGAL_BASIS_ID INTEGER NOT NULL,
    LEGAL_BASIS_DESCRIPTION TEXT,
    CONSTRAINT FK_POLICY_PURPOSE FOREIGN KEY (PURPOSE_ID)
        REFERENCES PURPOSE (PURPOSE_ID),
    CONSTRAINT FK_POLICY_LEGAL_BASIS FOREIGN KEY (LEGAL_BASIS_ID)
        REFERENCES LEGAL_BASIS (LEGAL_BASIS_ID)
);

