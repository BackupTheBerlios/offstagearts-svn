#!/bin/sh
#
# Create:
#  1. Server public/private key pair
#  2. Keystore to store client keys that the server is willing to connect to.

SERVERDIR=keys/server
SERVERSSLDIR=keys/server/ssl
CLIENTDIR=keys/client
SERVERSTORE=$SERVERDIR/server-store.jks
STOREPASS=keyst0re
KEYPASS=$STOREPASS

mkdir -p $SERVERSSLDIR
mkdir -p keys/client
mkdir -p $SERVERDIR

# Generate server's public/private key pair
\rm $SERVERSTORE
keytool -genkey -validity 36500 -alias serverprivate -keystore $SERVERSTORE -keyalg rsa -keysize 2048 -storepass $STOREPASS -keypass $KEYPASS <<EOF


OffstageArts


US
yes
EOF

# Self-sign certificate
keytool -export -rfc -alias serverprivate -keystore $SERVERSTORE -file $SERVERDIR/server.cer -storepass $STOREPASS


# ====================================================
# OpenSSL Conversion

# Export the private key
# Usage: DumpKey jks storepass alias keypass out
java -cp target/oassl-*.jar DumpKey $SERVERSTORE $STOREPASS serverprivate $KEYPASS $SERVERSSLDIR/server-private-bin.key

# Convert the key from binary to PEM format
PEM=$SERVERSSLDIR/server.pem
echo '-----BEGIN PRIVATE KEY-----' >$PEM
openssl enc -in $SERVERSSLDIR/server-private-bin.key -a >>$PEM
echo '-----END PRIVATE KEY-----' >>$PEM
echo >>$PEM

# Convert server certificate to PEM format (it's already in standard X.509
# format, just need to re-encode the bytes)
openssl enc -in $SERVERDIR/server.cer >>$PEM
chmod 600 $PEM
