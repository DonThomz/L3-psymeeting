/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

insert into patient (patient_id, name, last_name, gender, relationship, discovery_way, birthday)
values (1, 'Estele', 'Ivy', 'femme', 'célibataire', 'autre patient', TO_DATE('2000-05-10', 'yyyy-mm-dd'));
insert into patient (patient_id, name, last_name, gender, relationship, discovery_way, birthday)
values (2, 'Joaquin', 'Probyn', 'femme', 'couple', 'autres', TO_DATE('2005-06-29', 'yyyy-mm-dd'));
insert into patient (patient_id, name, last_name, gender, relationship, discovery_way, birthday)
values (3, 'George', 'Dickman', 'non binaire', 'couple', 'autre patient', TO_DATE('2015-08-01', 'yyyy-mm-dd'));
insert into patient (patient_id, name, last_name, gender, relationship, discovery_way, birthday)
values (4, 'Katina', 'Waitland', 'non binaire', 'couple', 'pages jaunes', TO_DATE('2006-05-12', 'yyyy-mm-dd'));
insert into patient (patient_id, name, last_name, gender, relationship, discovery_way, birthday)
values (5, 'Shurlocke', 'Decent', 'non binaire', 'couple', 'internet', TO_DATE('2004-06-26', 'yyyy-mm-dd'));

commit;