update ocdisccatids set name='studio' where name='studio_disc';
update ocdisccatids set name='teacher' where name='teacher_disc';

ALTER TABLE subsamt DROP CONSTRAINT subsamt_pkey;
ALTER TABLE subsamt ADD PRIMARY KEY (meetingid, entityid, ocdisccatid);

ALTER TABLE ocdiscids RENAME dtstart  TO dstart;
ALTER TABLE ocdiscids RENAME dtend  TO dend;
