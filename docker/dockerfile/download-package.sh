mkdir packages
cd packages
apt-get download $(apt-rdepends iputils-ping|grep -v "^ ")
apt-get download $(apt-rdepends telnet|grep -v "^ ")
apt-get download $(apt-rdepends curl|grep -v "^ ")
apt-get download net-tools
