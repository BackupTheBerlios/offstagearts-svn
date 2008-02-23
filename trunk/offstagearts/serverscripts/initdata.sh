#!/bin/sh
# Usage: initdata name

psql $1 -U postgres <<EOF

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('courseroles', 'courseroleid'), 3, true);
INSERT INTO courseroles (courseroleid, name, orderid) VALUES (1, 'student', 1);
INSERT INTO courseroles (courseroleid, name, orderid) VALUES (2, 'teacher', 2);
INSERT INTO courseroles (courseroleid, name, orderid) VALUES (3, 'pianist', 3);

COPY dbversion (major, minor, rev) FROM stdin;
0	3	0
\.

COPY paymenttypeids (paymenttypeid, "table", name) FROM stdin;
2	cashpayments	Cash
3	checkpayments	Check
1	ccpayments	Credit Card (MC/Visa)
\.


COPY phoneids (groupid, name, priority, letter, code) FROM stdin;
12	cell	2	\N	c
19	home	1	h	h
9	second fax	31	\N	f2
14	fax	30	\N	f
17	school	29	\N	s
15	emergency	28	\N	e
16	pager	27	\N	p
8	second number	3	\N	h2
18	work	4	e	w
10	second work	5	\N	w2
11	other	6	\N	o
13	extension	32	\N	x
\.

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('relprimarytypes', 'relprimarytypeid'), 12, true);


COPY relprimarytypes (relprimarytypeid, name) FROM stdin;
1	
2	child
3	cousin
4	dss
5	parent
6	grandchild
7	grandparent
8	niece
9	sibling
10	spouse
11	au pair
12	ex-spouse
\.

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('tickettypes', 'tickettypeid'), 8, true);


COPY tickettypes (tickettypeid, name) FROM stdin;
1	unknown
2	voucher
3	adult
4	company
5	free
6	senior
7	vip
8	usher
\.

COPY noteids (groupid, name) FROM stdin;
88	NOTES
\.

insert into termtypes (name) values ('Fall');
insert into termtypes (name) values ('Spring');
insert into termtypes (name) values ('Summer');
insert into termtypes (name) values ('Full Year');


INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (1, 'Sun', 'Su', 'Sunday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (2, 'Mon', 'M', 'Monday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (4, 'Wed', 'W', 'Wednesday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (6, 'Fri', 'F', 'Friday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (7, 'Sat', 'S', 'Saturday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (3, 'Tue', 'T', 'Tuesday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (5, 'Thr', 'R', 'Thursday');



SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('actypes', 'actypeid'), 4, true);
INSERT INTO actypes (actypeid, name) VALUES (1, 'school');
INSERT INTO actypes (actypeid, name) VALUES (2, 'ticket');
INSERT INTO actypes (actypeid, name) VALUES (3, 'pledge');
INSERT INTO actypes (actypeid, name) VALUES (4, 'openclass');


COPY mailprefids (mailprefid, name) FROM stdin;
1	Email Preferred
2	SnailMail Preferred
3	NO SnailMail
4	NO Email
5	NO MAIL AT ALL
\.

-- ========================================================
INSERT INTO duedateids (name, description) VALUES ('q2', 'Second Quarter Tuition');
INSERT INTO termduedates (name, description) VALUES ('q3', 'Third Quarter Tuition');
INSERT INTO termduedates (name, description) VALUES ('q4', 'Fourth Quarter Tuition');
INSERT INTO termduedates (name, description) VALUES ('q1', 'First Quarter Tuition');
INSERT INTO termduedates (name, description) VALUES ('y', 'Yearly Tuition');
INSERT INTO termduedates (name, description) VALUES ('r', 'Registration Fee');


insert into locations (name) values ('Main');


insert into actranstypes (name) values ('tuition');
insert into actranstypes (name) values ('latefee');
insert into actranstypes (name) values ('adj');
insert into actranstypes (name) values ('cash');
insert into actranstypes (name) values ('credit');
insert into actranstypes (name) values ('check');


EOF
