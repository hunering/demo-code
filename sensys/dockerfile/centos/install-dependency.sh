#/bin/bash

function installOSPackage {
	yum -y install libdb-devel libtool libtool-ltdl openssl openssl-devel
	yum -y install net-snmp net-snmp-utils net-snmp-python net-snmp-devel
	yum -y install postgresql-devel
	yum -y install python-devel
	yum -y install unzip
	yum -y install rpm-build
	yum -y install gcc-c++
	yum -y install which
	yum -y install git
	yum -y install make
	yum -y install flex
	yum -y install build-dep python-psycopg2
}

function installSigar {
	cd /root/sensys/packages

	#install sigar
	unzip -o sigar-master.zip
	cd sigar-master
	#https://www.gnu.org/software/automake/manual/html_node/Error-required-file-ltmain_002esh-not-found.html
	./autogen.sh
	#libtoolize
	./autogen.sh
	./configure
	make
	make install
}

function installIpmiUtil {
	cd /root/sensys/packages

	rpm -ivh ipmiutil-2.9.6-1.src.rpm
	
	cd /root/rpmbuild
	sed -i '/%configure --enable-systemd/c %configure --enable-systemd --enable-libsensors' ./SPECS/ipmiutil.spec 
	sed -i '/%configure/c %configure --enable-libsensors' ./SPECS/ipmiutil.spec 
	rpmbuild -bb ./SPECS/ipmiutil.spec
	rpm -ivh ./RPMS/x86_64/ipmiutil-2.9.6-1.el7.centos.x86_64.rpm
	rpm -ivh ./RPMS/x86_64/ipmiutil-devel-2.9.6-1.el7.centos.x86_64.rpm

}

function installPgSql {
	#Install PostgreSQL yum repository
	yum -y install http://yum.postgresql.org/9.3/redhat/rhel-7.2-x86_64/pgdg-centos93-9.3-2.noarch.rpm

	#install pgsql itself
	yum -y install postgresql93-server postgresql93-contrib postgresql93-client postgresql93-devel pgadmin3_93
}

function initPgSql {
	/usr/pgsql-9.3/bin/postgresql93-setup initdb
	systemctl enable postgresql-9.3.service
	systemctl start postgresql-9.3.service
	service postgresql-9.3 status
	
	
}

function configPgSql {
	echo ""
}

function createDB {
	sudo -u postgres createuser -P orcmuser
	sudo -u postgres createdb --owner orcmuser orcmdb

}

function installODBC {
	cd /root/sensys/packages
	tar -zxf ./unixODBC-2.3.4.tar.gz

	cd ./unixODBC-2.3.4	
	./configure 
	make 
	make install

	cd /root/sensys/packages
	tar -zxf ./psqlodbc-09.03.0400.tar.gz
	cd ./psqlodbc-09.03.0400
	./configure --with-unixodbc --with-libpq=/usr/pgsql-9.3 
	#./configure --with-unixodbc  
	make 
	make install
}


function disableFW {
	systemctl stop firewalld.service
	systemctl disable firewalld.service
}

installOSPackage
installSigar
installIpmiUtil
#We install and init postgres in another docker instance
installPgSql
#initPgSql
#configPgSql
#createDB
installODBC
disableFW
