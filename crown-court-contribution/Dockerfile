FROM amazoncorretto:21-alpine
RUN mkdir -p /opt/laa-crown-court-contribution/
WORKDIR /opt/laa-crown-court-contribution/
COPY ./build/libs/crown-court-contribution.jar /opt/laa-crown-court-contribution/app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8089 8098
CMD java -jar app.jar
