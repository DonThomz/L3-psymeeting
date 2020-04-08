---------------------------------------------------------------
--        Script Oracle.
---------------------------------------------------------------


------------------------------------------------------------
-- Table: patient
------------------------------------------------------------
CREATE TABLE patient(
 patient_id  NUMBER(10,0)  NOT NULL  ,
 email       VARCHAR2 (50) NOT NULL  ,
 password    VARCHAR2 (50) NOT NULL  ,
 name        VARCHAR2 (50) NOT NULL  ,
 last_name   VARCHAR2 (50) NOT NULL  ,
 gender      VARCHAR2 (50) NOT NULL  ,
 relationship   VARCHAR2 (50) NOT NULL  ,
 birthday    DATE  NOT NULL  ,
 discovery_way VARCHAR2 (50) NOT NULL ,
 CONSTRAINT patient_PK PRIMARY KEY (patient_id)
);

------------------------------------------------------------
-- Table: administrator
------------------------------------------------------------
CREATE TABLE administrator(
 administrator_id  NUMBER(10,0)  NOT NULL  ,
 name              VARCHAR2 (50) NOT NULL  ,
 last_name          VARCHAR2 (50) NOT NULL  ,
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

------------------------------------------------------------
-- Table: consultation
------------------------------------------------------------
CREATE TABLE consultation(
 consultation_id    NUMBER(10,0)  NOT NULL  ,
 consultation_date  DATE  NOT NULL  ,
 price              FLOAT  NOT NULL  ,
 paymode            VARCHAR2 (20) NOT NULL  ,

 CONSTRAINT consultation_PK PRIMARY KEY (consultation_id)
);


------------------------------------------------------------
-- Table: feedback
------------------------------------------------------------
CREATE TABLE feedback(
 feedback_id      NUMBER(10,0)  NOT NULL  ,
 indicator        NUMBER(10,0)   ,
 commentary        CLOB NOT NULL  ,
 keyword          CLOB   ,
 posture          CLOB   ,
 CONSTRAINT feedback_PK PRIMARY KEY (feedback_id)

 ,CONSTRAINT feedback_consultation_FK FOREIGN KEY (feedback_id) REFERENCES consultation(consultation_id)
);

------------------------------------------------------------
-- Table: carryOut
------------------------------------------------------------
CREATE TABLE consultation_carryOut(
 consultation_id  NUMBER(10,0)  NOT NULL  ,
 patient_id       NUMBER(10,0)  NOT NULL  ,
 CONSTRAINT carryOut_PK PRIMARY KEY (patient_id,consultation_id)

 ,CONSTRAINT carryOut_patient_FK FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
 ,CONSTRAINT carryOut_consultation_FK FOREIGN KEY (consultation_id) REFERENCES consultation(consultation_id)
);

commit;
