#/bin/bash
docker run --name sensys-postgres -e POSTGRES_PASSWORD=intel_123 -e POSTGRES_USER=postgres -d sensys-postgres

docker run --rm --name sensys-centos -h sensys-centos --dns=10.248.2.5 --link sensys-postgres:postgres -it sensys-centos /bin/bash
