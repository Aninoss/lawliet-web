FROM openjdk:11.0-jre

WORKDIR /home/app

COPY *.jar /home/app/app.jar

RUN useradd -m app

USER app

EXPOSE 8443

CMD [ "java", "-Xms800m", "-Xmx800m", "-Djava.awt.headless=true", "-jar", "app.jar" ]