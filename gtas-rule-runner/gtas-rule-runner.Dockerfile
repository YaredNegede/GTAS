FROM adoptopenjdk/maven-openjdk8

COPY ./gtas-rule-runner /gtas-rule-runner/

WORKDIR /gtas-rule-runner
RUN mvn clean install -Dmaven.test.skip=true

RUN apt-get -y update && \
    apt-get install wget

RUN wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz && \
    tar -C /usr/local/bin -xzvf dockerize-linux-amd64-v0.6.1.tar.gz

ENV RUN_ARGUMENTS ' --kb.list=${KB_LIST} \
                    --inbound.queue=${INBOUND_QUEUE} \
                    --outbound.queue=${OUTBOUND_QUEUE} \
                    --spring.datasource.url=${MAIRA_URL} \
                    --spring.datasource.username=${MARIA_USERNAME} \
                    --spring.datasource.password=${MARIA_PASSWORD} \
                    --activemq.broker.url=tcp://${ACTIVE_MQ_HOST}'

CMD mvn spring-boot:run -Dspring-boot.run.arguments="$RUN_ARGUMENTS" 