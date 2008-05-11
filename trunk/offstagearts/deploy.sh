#!/bin/sh

# Sign all the jarfiles
java -cp target/executable-netbeans.dir/offstagearts-*.jar offstage.licensor.SignJars

# Deploy to Java Web Start
rsync -avz jaws/signed/ citibob@offstagearts.org:/home/citibob/offstagearts.org/jars
rsync -avz jaws/jnlp/ citibob@offstagearts.org:/home/citibob/offstagearts.org/releases/offstagearts


#`java -cp target/executable-netbeans.dir/offstagearts-*.jar offstage.licensor.ReleaseVersion`

# Deploy to Ballet Theatre (quick but deprecated)
#rsync -avz target/executable-netbeans.dir/ rfischer@jmbt.merseine.nu:/Volumes/home/shared/Offstage/program

