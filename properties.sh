cd ./qf-crawler-against-server/src/main/resources;
mv application-env.yml application.yml;
mv bootstrap-env.yml bootstrap.yml;
cd ../docker;
echo "version="$1 > version.properties
