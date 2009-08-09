#!/bin/sh
# Runs OffstageArts from the config stored in a launcher

java -cp target/executable-netbeans.dir/offstagearts-*.jar offstage.launch.Custom $@

