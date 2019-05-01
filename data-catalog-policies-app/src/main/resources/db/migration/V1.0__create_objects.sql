CREATE SEQUENCE SEQ_LEGAL_BASIS;

CREATE SEQUENCE SEQ_POLICY;

CREATE SEQUENCE SEQ_PURPOSE;

CREATE TABLE  IF NOT EXISTS LEGAL_BASIS (
    LEGAL_BASIS_ID INTEGER DEFAULT NEXTVAL('SEQ_LEGAL_BASIS') PRIMARY KEY,
    DESCRIPTION VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS PURPOSE (
    PURPOSE_ID INTEGER DEFAULT NEXTVAL('SEQ_PURPOSE') PRIMARY KEY,
    PURPOSE_CODE VARCHAR(10) CONSTRAINT UK_PURPOSE_CODE UNIQUE,
    DESCRIPTION VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS BACKEND_SCHEMA.INFORMATION_TYPE (
    INFORMATION_TYPE_ID INTEGER NOT NULL,
    INFORMATION_TYPE_NAME VARCHAR(100) NOT NULL CONSTRAINT UK_INFORATION_TYPE UNIQUE,
    DESCRIPTION TEXT
);

CREATE TABLE IF NOT EXISTS POLICY (
    POLICY_ID INTEGER DEFAULT NEXTVAL('SEQ_POLICY') PRIMARY KEY,
    INFORMATION_TYPE_ID INTEGER NOT NULL,
    PURPOSE_ID INTEGER NOT NULL,
    LEGAL_BASIS_ID INTEGER NOT NULL,
    LEGAL_BASIS_DESCRIPTION TEXT,
    CONSTRAINT FK_POLICY_PURPOSE FOREIGN KEY (PURPOSE_ID)
        REFERENCES PURPOSE (PURPOSE_ID),
    CONSTRAINT FK_POLICY_LEGAL_BASIS FOREIGN KEY (LEGAL_BASIS_ID)
        REFERENCES LEGAL_BASIS (LEGAL_BASIS_ID)
);

