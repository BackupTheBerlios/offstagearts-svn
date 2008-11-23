--
-- PostgreSQL database dump
--

SET client_encoding = 'UNICODE';
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Name: actrans_actransid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('actrans', 'actransid'), 1, false);


--
-- Name: actrans_old_actransid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('actrans_old', 'actransid'), 1, false);


--
-- Name: actranstypes_actranstypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('actranstypes', 'actranstypeid'), 6, true);


--
-- Name: actypes_actypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('actypes', 'actypeid'), 4, true);


--
-- Name: ccbatches_ccbatchid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('ccbatches', 'ccbatchid'), 1, false);


--
-- Name: groupids_groupid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('groupids', 'groupid'), 1, false);


--
-- Name: courseids_courseid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('courseids', 'courseid'), 1, false);


--
-- Name: courseroles_courseroleid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('courseroles', 'courseroleid'), 3, true);


--
-- Name: coursesetids_coursesetid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('coursesetids', 'coursesetid'), 1, false);


--
-- Name: donations_serialid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('donations', 'serialid'), 1, false);


--
-- Name: duedateids_duedateid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('duedateids', 'duedateid'), 6, true);


--
-- Name: duedates_old_termid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

--SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('duedates_old', 'termid'), 1, false);


--
-- Name: entities_entityid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('entities', 'entityid'), 1, false);


--
-- Name: equeries_equeryid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('equeries', 'equeryid'), 1, false);


--
-- Name: holidays_holidayid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('holidays', 'holidayid'), 1, false);


--
-- Name: interests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('interests', 'id'), 1, false);


--
-- Name: invoiceids_invoiceid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('invoiceids', 'invoiceid'), 1, false);


--
-- Name: locations_locationid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('locations', 'locationid'), 1, true);


--
-- Name: mailprefids_mailprefid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('mailprefids', 'mailprefid'), 1, false);


--
-- Name: meetings_meetingid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('meetings', 'meetingid'), 1, false);


--
-- Name: offercodeids_offercodeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('offercodeids', 'offercodeid'), 1, false);


--
-- Name: paymentids_paymentid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('paymentids', 'paymentid'), 1, false);


--
-- Name: perftypeids_perftypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('perftypeids', 'perftypeid'), 1, false);


--
-- Name: phones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('phones', 'id'), 1, false);


--
-- Name: pplanids_pplanid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('pplanids', 'pplanid'), 1, false);


--
-- Name: programids_programid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('programids', 'programid'), 1, false);


--
-- Name: querylog_queryid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('querylog', 'queryid'), 1, false);


--
-- Name: relprimarytypes_relprimarytypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('relprimarytypes', 'relprimarytypeid'), 12, true);


--
-- Name: termids_old_termid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('termids_old', 'termid'), 1, false);


--
-- Name: termtypes_termtypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('termtypes', 'termtypeid'), 4, true);


--
-- Name: tickettypes_tickettypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('tickettypes', 'tickettypeid'), 8, true);


--
-- Name: venueids_venueid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('venueids', 'venueid'), 1, false);


--
-- Data for Name: absences; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: acbal; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: accounts; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: actrans; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: actrans_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: actranstypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO actranstypes (actranstypeid, name, description) VALUES (1, 'tuition', NULL);
INSERT INTO actranstypes (actranstypeid, name, description) VALUES (2, 'latefee', NULL);
INSERT INTO actranstypes (actranstypeid, name, description) VALUES (3, 'adj', NULL);
INSERT INTO actranstypes (actranstypeid, name, description) VALUES (4, 'cash', NULL);
INSERT INTO actranstypes (actranstypeid, name, description) VALUES (5, 'credit', NULL);
INSERT INTO actranstypes (actranstypeid, name, description) VALUES (6, 'check', NULL);
ALTER SEQUENCE actranstypes_actranstypeid_seq RESTART WITH 7;

--
-- Data for Name: actypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO actypes (actypeid, name) VALUES (1, 'school');
INSERT INTO actypes (actypeid, name) VALUES (2, 'ticket');
INSERT INTO actypes (actypeid, name) VALUES (3, 'pledge');
INSERT INTO actypes (actypeid, name) VALUES (4, 'openclass');
ALTER SEQUENCE actypes_actypeid_seq RESTART WITH 5;


--
-- Data for Name: adjpayments_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: attendance; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: cashpayments_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ccbatches; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ccpayments_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: checkpayments_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: classes; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: classids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: coursedeps; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: courseids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: courseroles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO courseroles (courseroleid, name, orderid) VALUES (1, 'student                       ', 1);
INSERT INTO courseroles (courseroleid, name, orderid) VALUES (2, 'teacher                       ', 2);
INSERT INTO courseroles (courseroleid, name, orderid) VALUES (3, 'pianist                       ', 3);
ALTER SEQUENCE courseroles_courseroleid_seq RESTART WITH 4;


--
-- Data for Name: coursesetids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: coursesets; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: daysofweek; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (1, 'Sun', 'Su', 'Sunday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (2, 'Mon', 'M', 'Monday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (4, 'Wed', 'W', 'Wednesday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (6, 'Fri', 'F', 'Friday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (7, 'Sat', 'S', 'Saturday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (3, 'Tue', 'T', 'Tuesday');
INSERT INTO daysofweek (javaid, shortname, lettername, longname) VALUES (5, 'Thr', 'R', 'Thursday');


--
-- Data for Name: dblogingroupids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: dblogingroups; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: dblogins; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: dbversion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO dbversion (major, minor, rev) VALUES (0, 3, 0);


--
-- Data for Name: donationids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: donations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: dtgroupids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: dtgroups; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: duedateids; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO duedateids (duedateid, name, description) VALUES (1, 'q2', 'Second Quarter Tuition');
INSERT INTO duedateids (duedateid, name, description) VALUES (2, 'q3', 'Third Quarter Tuition');
INSERT INTO duedateids (duedateid, name, description) VALUES (3, 'q4', 'Fourth Quarter Tuition');
INSERT INTO duedateids (duedateid, name, description) VALUES (4, 'q1', 'First Quarter Tuition');
INSERT INTO duedateids (duedateid, name, description) VALUES (5, 'y', 'Yearly Tuition');
INSERT INTO duedateids (duedateid, name, description) VALUES (6, 'r', 'Registration Fee');
ALTER SEQUENCE duedateids_duedateid_seq RESTART WITH 7;


--
-- Data for Name: duedates; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: duedates_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: dups; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: enrollments; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: entities; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: entities_school; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: equeries; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: eventids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: flagids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: flags; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: gradelevels; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: groupids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: holidays; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: interestids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: interests; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: invoiceids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: invoices; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO locations (locationid, name) VALUES (1, 'Main');


--
-- Data for Name: mailingids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: mailings; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: mailprefids; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO mailprefids (mailprefid, name) VALUES (1, 'Email Preferred');
INSERT INTO mailprefids (mailprefid, name) VALUES (2, 'SnailMail Preferred');
INSERT INTO mailprefids (mailprefid, name) VALUES (3, 'NO SnailMail');
INSERT INTO mailprefids (mailprefid, name) VALUES (4, 'NO Email');
INSERT INTO mailprefids (mailprefid, name) VALUES (5, 'NO MAIL AT ALL');
ALTER SEQUENCE mailprefids_mailprefid_seq RESTART WITH 6;


--
-- Data for Name: meetings; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: mergelog; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: noteids; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO noteids (groupid, name) VALUES (7, 'NOTES');


--
-- Data for Name: notes; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: offercodeids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: organizations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: paymentallocs; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: paymentids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: paymenttypeids; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO paymenttypeids (paymenttypeid, "table", name) VALUES (2, 'cashpayments', 'Cash');
INSERT INTO paymenttypeids (paymenttypeid, "table", name) VALUES (3, 'checkpayments', 'Check');
INSERT INTO paymenttypeids (paymenttypeid, "table", name) VALUES (1, 'ccpayments', 'Credit Card (MC/Visa)');
--ALTER SEQUENCE paymenttypeids_paymenttypeid_seq RESTART WITH 4;


--
-- Data for Name: perftypeids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: persons; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: phoneids; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (12, 'cell', 2, NULL, 'c');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (19, 'home', 1, 'h', 'h');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (9, 'second fax', 31, NULL, 'f2');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (14, 'fax', 30, NULL, 'f');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (17, 'school', 29, NULL, 's');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (15, 'emergency', 28, NULL, 'e');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (16, 'pager', 27, NULL, 'p');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (8, 'second number', 3, NULL, 'h2');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (18, 'work', 4, 'e', 'w');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (10, 'second work', 5, NULL, 'w2');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (11, 'other', 6, NULL, 'o');
INSERT INTO phoneids (groupid, name, priority, letter, code) VALUES (13, 'extension', 32, NULL, 'x');


--
-- Data for Name: phones; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: pplanids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: pplaninvoiceids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: pplantypeids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: programids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: querylog; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: querylogcols; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: regelig; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: registrations; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: relprimarytypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (1, '');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (2, 'child');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (3, 'cousin');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (4, 'dss');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (5, 'parent');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (6, 'grandchild');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (7, 'grandparent');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (8, 'niece');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (9, 'sibling');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (10, 'spouse');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (11, 'au pair');
INSERT INTO relprimarytypes (relprimarytypeid, name) VALUES (12, 'ex-spouse');
ALTER SEQUENCE relprimarytypes_relprimarytypeid_seq RESTART WITH 13;


--
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: statusids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: subs; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: termids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: termids_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: termregs; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: termregs_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: termtypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO termtypes (termtypeid, name, orderid) VALUES (1, 'Fall', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (2, 'Spring', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (3, 'Summer', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (4, 'Full Year', NULL);
ALTER SEQUENCE termtypes_termtypeid_seq RESTART WITH 5;


--
-- Data for Name: ticketeventids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: ticketeventsales; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: tickettypes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tickettypes (tickettypeid, name) VALUES (1, 'unknown');
INSERT INTO tickettypes (tickettypeid, name) VALUES (2, 'voucher');
INSERT INTO tickettypes (tickettypeid, name) VALUES (3, 'adult');
INSERT INTO tickettypes (tickettypeid, name) VALUES (4, 'company');
INSERT INTO tickettypes (tickettypeid, name) VALUES (5, 'free');
INSERT INTO tickettypes (tickettypeid, name) VALUES (6, 'senior');
INSERT INTO tickettypes (tickettypeid, name) VALUES (7, 'vip');
INSERT INTO tickettypes (tickettypeid, name) VALUES (8, 'usher');
ALTER SEQUENCE tickettypes_tickettypeid_seq RESTART WITH 9;


--
-- Data for Name: tuitiontrans_old; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: venueids; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: zztest; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER SEQUENCE groupids_groupid_seq RESTART WITH 20;

--
-- PostgreSQL database dump complete
--




