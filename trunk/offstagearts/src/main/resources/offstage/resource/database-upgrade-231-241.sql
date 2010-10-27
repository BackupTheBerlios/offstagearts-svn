-- update donations set amount=0 where amount is null;
-- ALTER TABLE donations
--    ALTER COLUMN amount SET NOT NULL;

COMMENT ON COLUMN donations.amount IS 'Just the tax-deductible amount.';

ALTER TABLE donations ADD COLUMN amountnondeduct numeric(9,2);
COMMENT ON COLUMN donations.amountnondeduct IS 'Just the non-tax-deductible amount.';

ALTER TABLE donations ADD COLUMN numberoftickets int;

ALTER TABLE eventids ADD COLUMN date date;
update eventids set date = '01/01/1990';
alter table eventids alter column date set not null;


alter table ticketeventids add column startdate date;
update ticketeventids set startdate = '01/01/1990';
alter table ticketeventids alter column startdate set not null;

