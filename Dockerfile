FROM adoptopenjdk:15.0.2_7-jdk-openj9-0.24.0

WORKDIR /home/app

COPY lawliet-web-latest.jar /home/app/app.jar

RUN useradd -m app

USER app

EXPOSE 8443

CMD [ "java", "-jar", "app.jar" ]