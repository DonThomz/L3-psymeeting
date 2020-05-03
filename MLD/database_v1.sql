/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

---------------------------------------------------------------
--        Script Oracle.
---------------------------------------------------------------


------------------------------------------------------------
-- Table: patient
------------------------------------------------------------
CREATE TABLE patient(
                        patient_id     NUMBER(10,0)  NOT NULL  ,
                        name           VARCHAR2 (50) NOT NULL  ,
                        last_name      VARCHAR2 (50) NOT NULL  ,
                        birthday       DATE   ,
                        gender         VARCHAR2 (50)  ,
                        relationship   VARCHAR2 (50)  ,
                        discovery_way  VARCHAR2 (50)  ,
                        CONSTRAINT patient_PK PRIMARY KEY (patient_id)
);

------------------------------------------------------------
-- Table: jobs
------------------------------------------------------------
CREATE TABLE jobs(
                     jobs_id   NUMBER(10,0)  NOT NULL  ,
                     job_name  VARCHAR2 (50) NOT NULL  ,
                     CONSTRAINT jobs_PK PRIMARY KEY (jobs_id)
);

------------------------------------------------------------
-- Table: consultation
------------------------------------------------------------
CREATE TABLE consultation(
                             consultation_id    NUMBER(10,0)  NOT NULL  ,
                             consultation_date  DATE  NOT NULL  ,
                             price              FLOAT   ,
                             pay_mode           VARCHAR2 (20)  ,
                             anxiety            NUMBER (1) NOT NULL  ,
                             CONSTRAINT consultation_PK PRIMARY KEY (consultation_id),
                             CONSTRAINT CHK_BOOLEAN_anxiety CHECK (anxiety IN (0,1))
);

------------------------------------------------------------
-- Table: feedback
------------------------------------------------------------
CREATE TABLE feedback(
                         feedback_id      NUMBER(10,0)  NOT NULL  ,
                         indicator        NUMBER(10,0)   ,
                         consultation_id  NUMBER(10,0)  NOT NULL  ,
                         CONSTRAINT feedback_PK PRIMARY KEY (feedback_id)

    ,CONSTRAINT feedback_consultation_FK FOREIGN KEY (consultation_id) REFERENCES consultation(consultation_id)
    ,CONSTRAINT feedback_consultation_AK UNIQUE (consultation_id)
);

------------------------------------------------------------
-- Table: user_app
------------------------------------------------------------
CREATE TABLE user_app(
                         user_id     NUMBER(10,0)  NOT NULL  ,
                         email       VARCHAR2 (50) NOT NULL  ,
                         password    VARCHAR2 (50) NOT NULL  ,
                         patient_id  NUMBER(10,0)  NOT NULL  ,
                         CONSTRAINT user_app_PK PRIMARY KEY (user_id)

    ,CONSTRAINT user_app_patient_FK FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
    ,CONSTRAINT user_app_patient_AK UNIQUE (patient_id)
);

------------------------------------------------------------
-- Table: keyword
------------------------------------------------------------
CREATE TABLE keyword(
                        keyword_id   NUMBER(10,0)  NOT NULL  ,
                        keyword      CLOB  NOT NULL  ,
                        feedback_id  NUMBER(10,0)  NOT NULL  ,
                        CONSTRAINT keyword_PK PRIMARY KEY (keyword_id)

    ,CONSTRAINT keyword_feedback_FK FOREIGN KEY (feedback_id) REFERENCES feedback(feedback_id)
);

------------------------------------------------------------
-- Table: commentary
------------------------------------------------------------
CREATE TABLE commentary(
                           commentary_id  NUMBER(10,0)  NOT NULL  ,
                           commentary     CLOB  NOT NULL  ,
                           feedback_id    NUMBER(10,0)  NOT NULL  ,
                           CONSTRAINT commentary_PK PRIMARY KEY (commentary_id)

    ,CONSTRAINT commentary_feedback_FK FOREIGN KEY (feedback_id) REFERENCES feedback(feedback_id)
);

------------------------------------------------------------
-- Table: posture
------------------------------------------------------------
CREATE TABLE posture(
                        posture_id   NUMBER(10,0)  NOT NULL  ,
                        posture      CLOB  NOT NULL  ,
                        feedback_id  NUMBER(10,0)  NOT NULL  ,
                        CONSTRAINT posture_PK PRIMARY KEY (posture_id)

    ,CONSTRAINT posture_feedback_FK FOREIGN KEY (feedback_id) REFERENCES feedback(feedback_id)
);

------------------------------------------------------------
-- Table: patientjob
------------------------------------------------------------
CREATE TABLE patientjob(
                           jobs_id     NUMBER(10,0)  NOT NULL  ,
                           patient_id  NUMBER(10,0)  NOT NULL  ,
                           job_date    DATE  NOT NULL  ,
                           CONSTRAINT patientjob_PK PRIMARY KEY (jobs_id,patient_id)

    ,CONSTRAINT patientjob_jobs_FK FOREIGN KEY (jobs_id) REFERENCES jobs(jobs_id)
    ,CONSTRAINT patientjob_patient0_FK FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

------------------------------------------------------------
-- Table: consultation_carryout
------------------------------------------------------------
CREATE TABLE consultation_carryout(
                                      patient_id       NUMBER(10,0)  NOT NULL  ,
                                      consultation_id  NUMBER(10,0)  NOT NULL  ,
                                      CONSTRAINT consultation_carryout_PK PRIMARY KEY (patient_id,consultation_id)

    ,CONSTRAINT consultation_carryout_patient_FK FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
    ,CONSTRAINT consultation_carryout_consultation0_FK FOREIGN KEY (consultation_id) REFERENCES consultation(consultation_id)
);


commit;






