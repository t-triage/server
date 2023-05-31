FROM openjdk:jre-alpine

ADD target/clarolab-ttriage.jar /app/app.jar
RUN apk add --no-cache fontconfig ttf-dejavu
CMD ["java", "-Xmx2048M", "-Xms1024M", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/logs", "-Dserver.port=80", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]

EXPOSE 80:80
