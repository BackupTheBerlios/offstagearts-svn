-- convert dupok to action
ALTER TABLE mergelog ADD COLUMN action int NOT NULL DEFAULT 0;
update mergelog set action = 2 where not dupok;
ALTER TABLE mergelog DROP COLUMN dupok;

-- allow for provisional actions, subject to later review and approval.
ALTER TABLE mergelog ADD COLUMN provisional boolean NOT NULL DEFAULT false;
