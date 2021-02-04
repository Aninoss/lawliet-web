FROM openjdk:11.0-jre

WORKDIR /home/app

COPY *.jar /home/app/app.jar

RUN useradd -m app

USER app

EXPOSE 8443

CMD [ "java", "-jar", "app.jar" ]