/*
 * Copyright (c) 2020. Thomas GUILLAUME & Gabriel DUGNY
 */

insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (1, TO_DATE('2020-03-25 17:00:00', 'yyyy-mm-dd HH24:MI:ss'), 50, 'carte bleue');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (2, TO_DATE('2020-03-14 18:00:00', 'yyyy-mm-dd HH24:MI:ss'), 30, 'espèce');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (3, TO_DATE('2020-03-15 19:00:00', 'yyyy-mm-dd HH24:MI:ss'), 30, 'espèce');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (4, TO_DATE('2020-04-16 08:00:00', 'yyyy-mm-dd HH24:MI:ss'), 30, 'chèque');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (5, TO_DATE('2020-04-21 08:30:00', 'yyyy-mm-dd HH24:MI:ss'), 30, 'chèque');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (6, TO_DATE('2020-04-22 09:00:00', 'yyyy-mm-dd HH24:MI:ss'), 30, 'chèque');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (7, TO_DATE('2020-04-23 10:30:00', 'yyyy-mm-dd HH24:MI:ss'), 30, 'carte bleue');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (8, TO_DATE('2020-04-24 11:00:00', 'yyyy-mm-dd HH24:MI:ss'), 50, 'chèque');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (9, TO_DATE('2020-04-24 18:30:00', 'yyyy-mm-dd HH24:MI:ss'), 50, 'espèce');
insert into consultation (consultation_id, consultation_date, price, pay_mode)
values (10, TO_DATE('2020-04-25 19:00:00', 'yyyy-mm-dd HH24:MI:ss'), 50, 'carte bleue');

commit;