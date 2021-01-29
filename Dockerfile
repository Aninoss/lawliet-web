FROM openjdk:11

WORKDIR /home/app

COPY target/lawliet-web-2.0-SNAPSHOT.jar /home/app/

RUN useradd -m app

USER app

EXPOSE 8443

CMD [ "java", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=75.0", "-XX:MaxRAMPercentage=75.0", "-Djava.awt.headless=true", "-jar", "lawliet-web-2.0-SNAPSHOT.jar" ]