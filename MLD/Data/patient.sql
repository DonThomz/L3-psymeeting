/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

insert into patient (name, last_name, gender, relationship, discovery_way, birthday)
values ('Estele', 'Ivy', 'Femme', 'Célibataire', 'Autre patient', TO_DATE('2000-05-10', 'yyyy-mm-dd'));
insert into patient (name, last_name, gender, relationship, discovery_way, birthday)
values ('Joaquin', 'Probyn', 'Femme', 'couple', 'Autres', TO_DATE('2005-06-29', 'yyyy-mm-dd'));
insert into patient (name, last_name, gender, relationship, discovery_way, birthday)
values ('George', 'Dickman', 'Non binaire', 'Couple', 'autre patient', TO_DATE('2015-08-01', 'yyyy-mm-dd'));
insert into patient (name, last_name, gender, relationship, discovery_way, birthday)
values ('Katina', 'Waitland', 'Non binaire', 'Couple', 'pages jaunes', TO_DATE('2006-05-12', 'yyyy-mm-dd'));
insert into patient (name, last_name, gender, relationship, discovery_way, birthday)
values ('Shurlocke', 'Decent', 'Non binaire', 'Couple', 'internet', TO_DATE('2004-06-26', 'yyyy-mm-dd'));

commit;