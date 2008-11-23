update entities set dobapprox = false where dobapprox is null;

ALTER TABLE entities
   ALTER COLUMN dobapprox SET NOT NULL;
