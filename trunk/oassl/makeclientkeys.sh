#!/bin/sh
#

SERVERDIR=`pwd`/keys/server
CLIENTDIR=`pwd`/keys/client
USERNAME=$1
CLIENTSTORE=$CLIENTDIR/$USERNAME-store.jks
CLIENTTRUST=$CLIENTDIR/$USERNAME-trust.jks
SERVERTRUST=$SERVERDIR/server-trust.jks
STOREPASS=keyst0re
KEYPASS=$STOREPASS
SERVERSSLDIR=$SERVERDIR/ssl
CLIENTSSLDIR=$CLIENTDIR/ssl

mkdir -p $CLIENTDIR
mkdir -p $SERVERDIR
mkdir -p $CLIENTSSLDIR
mkdir -p $SERVERSSLDIR/capath

\rm $CLIENTSTORE
keytool -genkey -validity 36500 -alias $USERNAME-private -keystore $CLIENTSTORE -keyalg rsa -keysize 2048 -storepass $STOREPASS -keypass $KEYPASS <<EOF


$USERNAME


US
yes
EOF

# Self-sign certificate
keytool -rfc -export -alias $USERNAME-private -keystore $CLIENTSTORE -file $CLIENTDIR/$USERNAME.cer -storepass $STOREPASS -keypass $KEYPASS

# Import client into server's set of trusted certificates
keytool -delete -alias $USERNAME-cert -keystore $SERVERTRUST -storepass $STOREPASS -keypass $KEYPASS
keytool -import -alias $USERNAME-cert -file $CLIENTDIR/$USERNAME.cer -keystore $SERVERTRUST -storepass $STOREPASS -keypass $KEYPASS <<EOF
yes
EOF

# Import server into client's set of trusted sertificates
keytool -delete -alias server-cert -keystore $CLIENTTRUST -storepass $STOREPASS -keypass $KEYPASS
keytool -import -alias server-cert -file $SERVERDIR/server.cer -keystore $CLIENTTRUST -storepass $STOREPASS -keypass $KEYPASS <<EOF
yes
EOF

# ====================================================
# OpenSSL Conversion

# Convert client certificate to PEM format
#$CLIENTDIR/$USERNAME.cer
PEM=$CLIENTSSLDIR/$USERNAME.pem
openssl enc -in $CLIENTDIR/$USERNAME.cer >$PEM

# Import client into server's set of trusted keys
PEMHASH=$SERVERSSLDIR/capath/`openssl x509 -hash -noout -in $PEM`.0
rm -f $PEMHASH
ln -s $PEM $PEMHASH
