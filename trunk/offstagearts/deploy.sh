#!/bin/sh

# Sign the jars
for x in target/executable-netbeans.dir/*.jar target/executable-netbeans.dir/lib/*.jar
do
	echo $x
	jarsigner -storepass datatimekey $x offstagearts
done

# Deploy to Ballet Theatre (deprecated)
rsync -avz target/executable-netbeans.dir/ rfischer@jmbt.merseine.nu:/Volumes/home/shared/Offstage/program

# Deploy to Java Web Start (will change)
rsync -avz target/executable-netbeans.dir/ citibob@citibob.net:/home/citibob/citibob.net/offstagearts
