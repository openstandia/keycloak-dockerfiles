#!/bin/bash

########################################################################################
#
# Pre-processing
#
########################################################################################

# Dockerの版やバージョンによって、"host.docker.internal"が名前解決をしない場合がある。
# pingチェック後、名前解決出来ない場合は、hostsに追加する。
HOST_DOMAIN="host.docker.internal"
ping -q -c1 $HOST_DOMAIN > /dev/null 2>&1
if [ $? -ne 0 ]; then
  HOST_IP=$(ip route | awk 'NR==1 {print $3}')
  echo -e "$HOST_IP\t$HOST_DOMAIN" >> /etc/hosts
fi

# プロキシ設定を標準出力に出力する
cat /etc/httpd/conf.d/proxy.conf

########################################################################################
#
# Start HTTPD 
#
########################################################################################

exec /usr/sbin/httpd -DFOREGROUND 

# For debug
#tailf /dev/null 
