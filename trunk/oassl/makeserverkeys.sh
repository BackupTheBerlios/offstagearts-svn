#!/bin/sh
#
# Create:
#  1. Server public/private key pair
#  2. Keystore to store client keys that the server is willing to connect to.

SERVERSTORE=keys/server-store.jks
STOREPASS=keyst0re
KEYPASS=$STOREPASS

mkdir keys
\rm $SERVERSTORE
keytool -genkey -validity 36500 -alias serverprivate -keystore $SERVERSTORE -keyalg rsa -keysize 2048 -storepass $STOREPASS -keypass $KEYPASS <<EOF


OffstageArts


US
yes
EOF
keytool -export -rfc -alias serverprivate -keystore $SERVERSTORE -file server.cer -storepass $STOREPASS

