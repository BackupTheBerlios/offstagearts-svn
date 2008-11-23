update courseids set dayofweek = -1 where dayofweek is null;

ALTER TABLE courseids
   ALTER COLUMN dayofweek SET NOT NULL;

delete from courseids where termid is null;

ALTER TABLE courseids
   ALTER COLUMN termid SET NOT NULL;
