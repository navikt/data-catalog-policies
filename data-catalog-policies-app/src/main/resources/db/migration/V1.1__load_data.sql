-- Load data into LEGAL_BASIS
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('Folketrygdloven §§ 21-3: Medlemmets opplysningsplikt', 'Flyway', now());
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('Folketrygdloven §§ 21-4: Innhenting av opplysninger og uttalelser', 'Flyway', now());
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('folketrygdloven §§ 21-7: uriktige opplysninger', 'Flyway', now());
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('NAV loven § 7 a: Tilgang til opplysninger fra Folkeregisteret', 'Flyway', now());
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('Folketrygdloven § 22-15: Tilbakekreving etter feilaktig utbetaling', 'Flyway', now());
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('Forvaltningsloven § 35: Fornyet behandling av tidligere avgjorte saker', 'Flyway', now());
INSERT INTO LEGAL_BASIS (DESCRIPTION, CREATED_BY, CREATED_DATE)
VALUES('Arbeidsmarkedsloven § 19: Opplysningsplikt overfor Arbeids- og velferdsetaten', 'Flyway', now());

--Load data into PURPOSE
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('AAP','Arbeidsavklaringspenger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('AAR','Aa-registeret', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('AGR','Ajourhold - Grunnopplysninger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('BAR','Barnetrygd', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('BID','1 - Bidrag', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('BII','2 - Bidrag innkreving', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('BIL','Bil', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('DAG','Dagpenger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('ENF','Enslig forsørger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('ERS','Erstatning', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('FAR','Farskap', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('FEI','Feilutbetaling', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('FOR','Foreldre- og svangerskapspenger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('FOS','Forsikring', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('FUL','Fullmakt', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('GEN','Generell', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('GRA','Gravferdsstønad', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('GRU','Grunn- og hjelpestønad', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('HEL','Helsetjenester og ort. Hjelpemidler', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('HJE','Hjelpemidler', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('IAR','Inkluderende Arbeidsliv', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('IND','Individstønad', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('KLA','Klage/Anke', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('KNA','Kontakt NAV', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('KOM','Kommunale tjenester', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('KON','Kontantstøtte', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('KTR','Kontroll', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('LGA','Lønnsgaranti', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('MED','Medlemskap', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('MOB','Mob.stønad', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('MOT','3 - Skanning', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('OKO','Økonomi', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('OMS','Omsorgspenger, Pleiepenger og opplæringspenger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('OPA','Oppfølging - Arbeidsgiver', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('OPP','Oppfølging', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('OVR','4 - Øvrig', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('PEN','Pensjon', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('PER','Permittering og masseoppsigelser', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('REH','Rehabilitering', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('REK','Rekruttering og Stilling', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('RPO','Retting av personopplysninger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('RVE','Rettferdsvederlag', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SAA','Sanksjon - Arbeidsgiver', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SAK','Saksomkostning', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SAP','Sanksjon - Person', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SER','Serviceklager', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SIK','Sikkerhetstiltak', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('STO','Regnskap/utbetaling', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SUP','Supplerende stønad', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SYK','Sykepenger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('SYM','Sykemeldinger', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('TIL','Tiltak', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('TRK','Trekkhåndtering', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('TRY','Trygdeavgift', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('TSO','Tilleggsstønad', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('TSR','Tilleggsstønad arbeidssøkere', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('UFM','Unntak fra medlemskap', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('UFO','Uføretrygd', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('UKJ','Ukjent', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('VEN','Ventelønn', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('YRA','Yrkesrettet attføring', 'Flyway', now());
Insert into PURPOSE (PURPOSE_CODE,DESCRIPTION, CREATED_BY, CREATED_DATE) values ('YRK','Yrkesskade / Menerstatning', 'Flyway', now());
