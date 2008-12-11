-- let's think a bit more
-- about what fields we want before putting it into the database in general.
ALTER TABLE termregs ADD COLUMN emer_rel character varying(100);
ALTER TABLE termregs ADD COLUMN emer_name character varying(100);
ALTER TABLE termregs ADD COLUMN emer_addr character varying(100);
ALTER TABLE termregs ADD COLUMN emer_city character varying(50);
ALTER TABLE termregs ADD COLUMN emer_state character varying(20);
ALTER TABLE termregs ADD COLUMN emer_home character varying(30);
ALTER TABLE termregs ADD COLUMN emer_work character varying(30);
ALTER TABLE termregs ADD COLUMN emer_cell character varying(30);
ALTER TABLE termregs ADD COLUMN emer_doctorname character varying(100);
ALTER TABLE termregs ADD COLUMN emer_healthins character varying(50);
ALTER TABLE termregs ADD COLUMN emer_healthinsno character varying(70);
ALTER TABLE termregs ADD COLUMN med_pasttreatment character varying(500);
ALTER TABLE termregs ADD COLUMN med_curcondition character varying(500);
ALTER TABLE termregs ADD COLUMN med_allergies character varying(500);
ALTER TABLE termregs ADD COLUMN med_allergymeds character varying(500);
ALTER TABLE termregs ADD COLUMN med_curmeds character varying(500);
ALTER TABLE termregs ADD COLUMN med_tetboosterdate character varying(50);
ALTER TABLE termregs ADD COLUMN emer_filledout boolean NOT NULL DEFAULT false;
ALTER TABLE termregs ADD COLUMN emer_signed boolean NOT NULL DEFAULT false;

