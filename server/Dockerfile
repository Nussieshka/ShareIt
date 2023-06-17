FROM amazoncorretto:11

# Это необходимо, поскольку база данных не успевает запускаться до конца
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY target/*.jar server.jar
ENTRYPOINT ["./wait-for-it.sh", "postgres:5432", "--", "java", "-jar", "server.jar"]