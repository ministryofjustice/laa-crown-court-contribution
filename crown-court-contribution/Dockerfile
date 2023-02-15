FROM openjdk:17-jdk-slim
RUN mkdir -p /opt/laa-crown-court-contribution/
WORKDIR /opt/laa-crown-court-contribution/
COPY ./build/libs/crown-court-contribution.jar /opt/laa-crown-court-contribution/app.jar
RUN useradd -r -u 1001 appuser
USER 1001
EXPOSE 8080 8096
ENTRYPOINT ["java","-jar","app.jar"]
