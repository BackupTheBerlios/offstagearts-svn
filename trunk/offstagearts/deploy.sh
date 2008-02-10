#!/bin/sh

# # Sign the jars
# for x in target/executable-netbeans.dir/*.jar target/executable-netbeans.dir/lib/*.jar
# do
# 	echo $x
# 	jarsigner -storepass datatimekey $x offstagearts
# done
# 
# # Deploy to Java Web Start
# rsync -avz target/executable-netbeans.dir/ citibob@offstagearts.org:/home/citibob/offstagearts.org/releases/offstagearts/trunk

# Deploy to Ballet Theatre (quick but deprecated)
rsync -avz target/executable-netbeans.dir/ rfischer@jmbt.merseine.nu:/Volumes/home/shared/Offstage/program

