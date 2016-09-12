#/bin/bash
sed -i "$ a export http_proxy=${internal_http_proxy}" /root/.bashrc
sed -i "$ a export https_proxy=${internal_https_proxy}" /root/.bashrc
sed -i '$ a export no_proxy="localhost,127.0.0.1,192.168.17.0/24,10.239.0.0/16"' /root/.bashrc

sed -i '$ a C_INCLUDE_PATH=/usr/pgsql-9.3/include:$C_INCLUDE_PATH' /root/.bashrc
sed -i '$ a export C_INCLUDE_PATH' /root/.bashrc

sed -i '$ a export PATH=$PATH:/opt/open-rcm27/bin' /root/.bashrc
sed -i '$ a export LD_LIBRARY_PATH=/usr/local/lib:/usr/lib64:/usr/pgsql-9.3/lib/:$LD_LIBRARY_PATH' /root/.bashrc
sed -i '$ a export LIBRARY_PATH=/usr/local/lib:/usr/lib64:/usr/pgsql-9.3/lib/:$LIBRARY_PATH' /root/.bashrc

source /root/.bashrc

sed -i "$ a proxy=${internal_http_proxy}" /etc/yum.conf
