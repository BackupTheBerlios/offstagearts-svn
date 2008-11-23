CONF=stunnel.conf
PIDFILE=`pwd`/stunnel.pid

echo "pid = $PIDFILE" >$CONF
cat >>$CONF <<EOF
foreground = yes

[postgresql]
accept = 5433
connect = 5432
; Contains the set of client certificates
CApath = keys/server/ssl/capath
; Contains the server's self-signed certificate and private key
cert = keys/server/ssl/server.pem
verify = 3
EOF

stunnel $CONF
