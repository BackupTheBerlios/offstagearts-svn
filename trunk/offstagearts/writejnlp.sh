#!/bin/sh

# Usage:
#   deploy.sh release
#   deploy.sh latest

# Sign all the jarfiles
java -cp target/executable-netbeans.dir/offstagearts-*.jar offstage.licensor.SignJars $1
java -cp target/executable-netbeans.dir/offstagearts-*.jar offstage.licensor.WriteJNLP $1
