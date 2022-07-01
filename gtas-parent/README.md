maven wrapper was added to this project with:

mvn -N io.takari:maven:wrapper



###DOCKER


docker-compose -f elk-docker-compose.yml -f neo4j-etl-docker-compose.yml -f docker-compose.yml -f local-deployment.yml pull

docker-compose -f elk-docker-compose.yml -f neo4j-etl-docker-compose.yml -f docker-compose.yml -f local-deployment.yml up