# Build stage
FROM maven:3-eclipse-temurin-21 as build

RUN useradd -m myuser
WORKDIR /usr/src/app/
RUN chown myuser:myuser /usr/src/app/
USER myuser

COPY --chown=myuser pom.xml ./
RUN mvn dependency:go-offline -Pproduction

COPY --chown=myuser:myuser src src
COPY --chown=myuser:myuser package.json ./
COPY --chown=myuser:myuser package-lock.json* webpack.config.js* ./

RUN mvn clean package -DskipTests -Pproduction

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

COPY --from=build /usr/src/app/target/*.jar /home/app/app.jar
RUN useradd -m myuser
USER myuser

EXPOSE 8443
CMD ["java", "-jar", "/home/app/app.jar"]