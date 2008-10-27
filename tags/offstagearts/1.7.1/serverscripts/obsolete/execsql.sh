#psql ballettheatre -U ballettheatre <$1
cat $1 | ssh rfischer@jmbt.merseine.nu  /Library/PostgreSQL8/bin/psql ballettheatre -U ballettheatre
psql fscprotestants -U fscprotestants <$1
psql yfsc -U yfsc <$1
