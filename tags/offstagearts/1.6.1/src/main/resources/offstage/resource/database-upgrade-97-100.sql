-- Bug fix
ALTER TABLE courseroles ALTER "name" TYPE character varying(30);

-- Stores ultimate pricing and discount information on what a student payed
-- (and how it should be split up) when registering for a class.  Each time
-- a student signs into an open class, one or more of these recrods is made.
create table subsamt (
  meetingid integer NOT NULL,
  entityid integer NOT NULL,
  ocdisccatid integer NOT NULL,
  dollars numeric(9,2),
  PRIMARY KEY(meetingid, entityid),
   FOREIGN KEY (meetingid,entityid)
      REFERENCES subs (meetingid,entityid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);
--  CONSTRAINT subsamt_fkey 


-- Open Class pricing code can use this to figure out proportion of
-- teacher to studio profit sharing.  Teacher payroll can also use it
-- to figure out how much to pay teachers.  None of this stuff is
-- really set in stone, these are just parameters used by the final
-- report generator to calculate with.
create table teachers (
entityid int primary key,
displayname varchar(30),	-- Name to display on web site and other class listings
ocpct numeric(9,2),			-- % of total that teacher takes home for open class
hourlyrate numeric(9,2),		-- hourly rate teacher is paid, if used
perclassrate numeric(9,2)

);


ALTER TABLE enrollments DROP COLUMN pplanid;
ALTER TABLE enrollments DROP COLUMN dtapproved;
ALTER TABLE enrollments DROP COLUMN dtenrolled;

CREATE TABLE uniqenrolls
(
  courseid integer NOT NULL,
  entityid integer NOT NULL,
  courserole integer,
  dstart date,
  dend date,
  PRIMARY KEY (courseid, courserole)
);

alter table subs drop column dtapproved;
alter table subs drop column enterdtime;
alter table subs drop column payed;

CREATE TABLE uniqsubs
(
  meetingid integer NOT NULL,
  entityid integer NOT NULL,
  subtype character(1) NOT NULL, -- '+' or '-'
  courserole integer, -- If '+', role this person will play at this course meeting
  PRIMARY KEY (meetingid, courserole)
);

--create table teacherdisccodes (
--disccode varchar(30) not null,
--teacherid int not null,
--description varchar(100),
--pct numeric(9,2),
--primary key(disccode));
