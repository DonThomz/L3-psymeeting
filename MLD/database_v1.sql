---------------------------------------------------------------
--        Script Oracle.
---------------------------------------------------------------

/*
------------------------------------------------------------
-- Table: patient
------------------------------------------------------------
CREATE TABLE patient(
                        patient_id    NUMBER(10,0)  NOT NULL  ,
                        name          VARCHAR2 (50) NOT NULL  ,
                        last_name     VARCHAR2 (50) NOT NULL  ,
                        birthday      DATE ,
                        gender        VARCHAR2 (50)  ,
                        relationship  VARCHAR2 (50)  ,
                        discovery_way  VARCHAR2 (50)  ,
                        CONSTRAINT patient_PK PRIMARY KEY (patient_id)
);
/*
------------------------------------------------------------
-- Table: administrator
------------------------------------------------------------
CREATE TABLE administrator(
                              administrator_id  NUMBER(10,0)  NOT NULL  ,
                              name              VARCHAR2 (50) NOT NULL  ,
                              last_name         VARCHAR2 (50) NOT NULL  ,
                              password          VARCHAR2 (50) NOT NULL  ,
                              CONSTRAINT administrator_PK PRIMARY KEY (administrator_id)
);

------------------------------------------------------------
-- Table: jobs
------------------------------------------------------------
CREATE TABLE jobs(
                     jobs_id     NUMBER(10,0)  NOT NULL  ,
                     job_name    VARCHAR2 (50) NOT NULL  ,
                     job_date    DATE  NOT NULL  ,
                     patient_id  NUMBER(10,0)  NOT NULL  ,
                     CONSTRAINT jobs_PK PRIMARY KEY (jobs_id)

    ,CONSTRAINT jobs_patient_FK FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);
*/
------------------------------------------------------------
-- Table: consultation
------------------------------------------------------------
CREATE TABLE consultation(
                             consultation_id    NUMBER(10,0)  NOT NULL  ,
                             consultation_date  TIMESTAMP  NOT NULL  ,
                             price              FLOAT   ,
                             pay_mode           VARCHAR2 (20)  ,
                             CONSTRAINT consultation_PK PRIMARY KEY (consultation_id)
);
/*
------------------------------------------------------------
-- Table: feedback
------------------------------------------------------------
CREATE TABLE feedback(
                         feedback_id      NUMBER(10,0)  NOT NULL  ,
                         commentary       CLOB  NOT NULL  ,
                         keyword          CLOB   ,
                         posture          CLOB   ,
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
-- Table: consultation_carryout
------------------------------------------------------------
CREATE TABLE consultation_carryout(
                                      patient_id       NUMBER(10,0)  NOT NULL  ,
                                      consultation_id  NUMBER(10,0)  NOT NULL  ,
                                      CONSTRAINT consultation_carryout_PK PRIMARY KEY (patient_id,consultation_id)

    ,CONSTRAINT consultation_carryout_patient_FK FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
    ,CONSTRAINT consultation_carryout_consultation0_FK FOREIGN KEY (consultation_id) REFERENCES consultation(consultation_id)
);

*/


commit;
