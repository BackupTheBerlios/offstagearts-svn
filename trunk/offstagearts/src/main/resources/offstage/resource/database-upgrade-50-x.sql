insert into actranstypes (name) values ('openclass');

COMMENT ON COLUMN actrans.termid IS 'For school tuition: The term this is paying for.
For open class registrations: The meetingid of the class registered for.';

ALTER TABLE subs ADD COLUMN payed numeric(9, 2);
COMMENT ON COLUMN subs.payed IS 'Amount payed by student/payer to make this substitution.  Generally
0 for regular terms, but it will be the amount paid for open class
registrations.  This can handle complimentary and reduced-price open classes.';
