-- General Nonprofit Fundraising capabilities.
-- Put in for Discover Roxbury data transfer.  We felt this was
-- general enough to put into the main system.

-- Types of membership...
create table membershipids 
(primary key(groupid))
inherits (groupids);

-- Just one kind of member so far

-- Formal memberships in organization
insert into membershipids (name) values ('Member');

create table memberships (
  serialid serial NOT NULL,
  entityid integer NOT NULL,
  groupid integer NOT NULL,
  startdate date NOT NULL,
  enddate date NOT NULL,
primary key(serialid));


-- Volunteer / Staff+Intern, etc.  If startdate or enddate is null,
-- then there's no formal start or end of this relationship.
create table activityids (primary key(groupid)) inherits(groupids);

create table activities (
  serialid serial NOT NULL,
  entityid integer NOT NULL,
  groupid integer NOT NULL,
  startdate date,
  enddate date,
primary key(serialid));

-- What general "constituency" served by the organization this person
-- is a member of.  A person's "constituency" is, in a way, membership
-- in OTHER organizations (or demographic groups).  Relationship with
-- THIS org doesn't affect
-- the constituency.
create table constitids (primary key(groupid)) inherits(groupids);
create table constits (
  entityid integer NOT NULL,
  groupid integer NOT NULL,
primary key(entityid, groupid));


create table callprefids (callprefid serial primary key, name varchar(30));
	insert into callprefids (name) values ('Don''t Call');
create table mailstateids (mailstateid serial primary key, name varchar(30));
	insert into mailstateids (name) values ('incomplete');
	insert into mailstateids (name) values ('expired');
create table sourceids (sourceid serial primary key, name varchar(100),
date date, obsolete bool default false);

alter table entities add column nickname varchar(30);
alter table entities add column suffix varchar(20);
alter table entities add column askamount numeric(9,2);
alter table entities add column callprefid int;
alter table entities add column mailstateid int;
alter table entities add column sourceid int;

alter table donations add column donationtypeid int;

create table donationtypeids (donationtypeid serial, name varchar(30));
--insert into donationtypeids (name) values ('grant');
--insert into donationtypeids (name) values ('donation');

--insert into noteids (name) values ('email2');
