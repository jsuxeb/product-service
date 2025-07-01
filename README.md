#API-ORDER-SERVICE
#Docker Build
docker build -f Dockerfile -t galaxytraining/product-service:1.0.0 .

# DOCKER RUN EN WINDOWS (POWERSHELL)

docker run -d `
  --name product-service `
-p 8888:8888 `
  -e CONFIG_SCHEMA_REGISTRY_URL=http://192.168.1.48:8081 `
-e CONFIG_KAFKA_BROKERS=PLAINTEXT://192.168.1.48:19092 `
  -e MONGO_DB=mongodb://192.168.1.48:27017/products `
galaxytraining/product-service:1.0.0
