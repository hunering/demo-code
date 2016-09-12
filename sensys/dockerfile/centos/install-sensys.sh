#/bin/bash

function cloneCode {
	cd /root/sensys
	git config --global url.https://.insteadof git://
	git clone git://sid-gerrit.devtools.intel.com/css/grei
	cd ./grei
	git fetch
	git checkout -b remote_origin_v0.27 origin/v0.27
}

function configODBC {
	cd /root/sensys/grei
	sed -i '/Driver = <Path to the PostgreSQL ODBC driver>/c Driver = /usr/local/lib/psqlodbcw.so' ./contrib/database/psql_odbc_driver.ini
	
	odbcinst -i -d -f ./contrib/database/psql_odbc_driver.ini
	odbcinst -j

	sed -i '/Driver = <Name of the PostgreSQL driver>/c Driver = /usr/local/lib/psqlodbcw.so' ./contrib/database/orcmdb_psql.ini
	sed -i "/Servername = <Name or IP address of the database server>/c Servername = ${sensys_pg_server}" ./contrib/database/orcmdb_psql.ini
	
	odbcinst -i -s -f ./contrib/database/orcmdb_psql.ini -l

}

function buildSensys {
	cd /root/sensys/grei
	./autogen.pl
	./configure --prefix=/opt/open-rcm27 --with-platform=contrib/platform/intel/hillsboro/orcm-nightly-build --with-postgres=/usr/pgsql-9.3
	make all
	make install
}

function createDBSchema {
	cd /root/sensys/
	python ./packages/get-pip.py --proxy="${internal_http_proxy}"
	pip --proxy ${internal_http_proxy} install --upgrade SQLAlchemy
	pip --proxy ${internal_http_proxy} install --upgrade Alembic
	pip --proxy ${internal_http_proxy} install --upgrade psycopg2

	cd /root/sensys/grei/contrib/database
	export PG_DB_URL=postgresql://orcmuser:orcmuser@${sensys_pg_server}/orcmdb
	python setup_orcmdb.py --alembic-ini=schema_migration.ini
}

function initSensysCfg {
	sed -i "$ a db_postgres_uri = ${sensys_pg_server}:5432" /opt/open-rcm27/etc/openmpi-mca-params.conf
	sed -i '$ a db_postgres_database = orcmdb' /opt/open-rcm27/etc/openmpi-mca-params.conf
	sed -i '$ a db_postgres_user = orcmuser:orcmuser' /opt/open-rcm27/etc/openmpi-mca-params.conf

	rm -rf /opt/open-rcm27/etc/orcm-site.xml
	cp /root/sensys/orcm-site.xml /opt/open-rcm27/etc/
	sed -i "s/<host>controllername/<host>${sensys_servername}/" /opt/open-rcm27/etc/orcm-site.xml
	sed -i "s/<name>computenode1/<name>${sensys_servername}/" /opt/open-rcm27/etc/orcm-site.xml
	sed -i "s/<host>computenode1/<host>${sensys_servername}/" /opt/open-rcm27/etc/orcm-site.xml
	sed -i "s/<shost>scheduler/<shost>${sensys_servername}/" /opt/open-rcm27/etc/orcm-site.xml
}



cloneCode
configODBC
buildSensys
createDBSchema
initSensysCfg
