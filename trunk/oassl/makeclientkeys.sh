#!/bin/sh
#

USERNAME=$1
CLIENTSTORE=keys/$USERNAME-store.jks
CLIENTTRUST=keys/$USERNAME-trust.jks
SERVERTRUST=keys/server-trust.jks
STOREPASS=keyst0re
KEYPASS=$STOREPASS

\rm $CLIENTSTORE
keytool -genkey -validity 36500 -alias $USERNAME-private -keystore $CLIENTSTORE -keyalg rsa -keysize 2048 -storepass $STOREPASS -keypass $KEYPASS <<EOF


$USERNAME


US
yes
EOF

# Self-sign certificate
keytool -rfc -export -alias $USERNAME-private -keystore $CLIENTSTORE -file $USERNAME.cer -storepass $STOREPASS -keypass $KEYPASS

# Import client into server's set of trusted certificates
keytool -delete -alias $USERNAME-cert -keystore $SERVERTRUST -storepass $STOREPASS -keypass $KEYPASS
keytool -import -alias $USERNAME-cert -file $USERNAME.cer -keystore $SERVERTRUST -storepass $STOREPASS -keypass $KEYPASS <<EOF
yes
EOF

# Import server into client's set of trusted sertificates
keytool -delete -alias server-cert -keystore $CLIENTTRUST -storepass $STOREPASS -keypass $KEYPASS
keytool -import -alias server-cert -file server.cer -keystore $CLIENTTRUST -storepass $STOREPASS -keypass $KEYPASS <<EOF
yes
EOF

