------------------------------------------------------------
-- Script MySQL.
------------------------------------------------------------


-------------------------------------------------------------
-- Table: Patient
-------------------------------------------------------------

CREATE TABLE Patient(
        patient_id        Int NOT NULL ,
        nom               Varchar (50) NOT NULL ,
        prenom            Varchar (50) NOT NULL ,
        date_naissance    Date NOT NULL ,
        genre             Varchar (50) NOT NULL ,
        situation_sociale Varchar (50) NOT NULL
	,CONSTRAINT Patient_PK PRIMARY KEY (patient_id)
);


-------------------------------------------------------------
-- Table: Consultation
-------------------------------------------------------------

CREATE TABLE Consultation(
        consultation_id Int NOT NULL ,
        moyen           Varchar (50) NOT NULL ,
        patient_id      Int NOT NULL
	,CONSTRAINT Consultation_AK UNIQUE (patient_id)
	,CONSTRAINT Consultation_PK PRIMARY KEY (consultation_id)
);


-------------------------------------------------------------
-- Table: ConsultationAnxiete
-------------------------------------------------------------

CREATE TABLE ConsultationAnxiete(
        consultation_id Int NOT NULL ,
        indicateur      Int NOT NULL ,
        moyen           Varchar (50) NOT NULL ,
        patient_id      Int NOT NULL
	,CONSTRAINT ConsultationAnxiete_AK UNIQUE (patient_id)
	,CONSTRAINT ConsultationAnxiete_PK PRIMARY KEY (consultation_id)

	,CONSTRAINT ConsultationAnxiete_Consultation_FK FOREIGN KEY (consultation_id) REFERENCES Consultation(consultation_id)
);


-------------------------------------------------------------
-- Table: Professions
-------------------------------------------------------------

CREATE TABLE Professions(
        profession_id  Int NOT NULL ,
        profession_nom Varchar (5) NOT NULL ,
        date_debut     Date NOT NULL ,
        date_fin       Date NOT NULL ,
        patient_id     Int NOT NULL
	,CONSTRAINT Professions_PK PRIMARY KEY (profession_id)

	,CONSTRAINT Professions_Patient_FK FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);


------------------------------------------------------------
-- Table: MotCle
------------------------------------------------------------

CREATE TABLE MotCle(
        mot_cle_id Int NOT NULL ,
        mot_cle    Varchar (50) NOT NULL
	,CONSTRAINT MotCle_PK PRIMARY KEY (mot_cle_id)
);


------------------------------------------------------------
-- Table: Posture
--------------------------------------------------------------

CREATE TABLE Posture(
        posture_id  Int NOT NULL ,
        posture_nom Varchar (50) NOT NULL
	,CONSTRAINT Posture_PK PRIMARY KEY (posture_id)
);


-------------------------------------------------------------
-- Table: Comportement
-------------------------------------------------------------

CREATE TABLE Comportement(
        comportement_id Int NOT NULL ,
        commentaire     Varchar (50) NOT NULL
	,CONSTRAINT Comportement_PK PRIMARY KEY (comportement_id)
);


--------------------------------------------------------------
-- Table: Effectuer
-------------------------------------------------------------

CREATE TABLE Effectuer(
        consultation_id Int NOT NULL ,
        patient_id      Int NOT NULL ,
        consultation_date Date NOT NULL ,
        payement        Varchar (50) NOT NULL ,
        prix            Int NOT NULL
	,CONSTRAINT Effectuer_PK PRIMARY KEY (consultation_id,patient_id)

	,CONSTRAINT Effectuer_Consultation_FK FOREIGN KEY (consultation_id) REFERENCES Consultation(consultation_id)
	,CONSTRAINT Effectuer_Patient0_FK FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);


-------------------------------------------------------------
-- Table: Appartenir
-------------------------------------------------------------

CREATE TABLE Appartenir(
        mot_cle_id      Int NOT NULL ,
        patient_id      Int NOT NULL ,
        posture_id      Int NOT NULL ,
        comportement_id Int NOT NULL
	,CONSTRAINT Appartenir_PK PRIMARY KEY (mot_cle_id,patient_id,posture_id,comportement_id)

	,CONSTRAINT Appartenir_MotCle_FK FOREIGN KEY (mot_cle_id) REFERENCES MotCle(mot_cle_id)
	,CONSTRAINT Appartenir_Patient0_FK FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
	,CONSTRAINT Appartenir_Posture1_FK FOREIGN KEY (posture_id) REFERENCES Posture(posture_id)
	,CONSTRAINT Appartenir_Comportement2_FK FOREIGN KEY (comportement_id) REFERENCES Comportement(comportement_id)
);

