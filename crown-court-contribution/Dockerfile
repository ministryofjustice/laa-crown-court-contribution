FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/laa-crown-court-contribution/
WORKDIR /opt/laa-crown-court-contribution/
COPY ./build/libs/crown-court-contribution.jar /opt/laa-crown-court-contribution/app.jar
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
EXPOSE 8087 8098
ENTRYPOINT ["java","-jar","app.jar"]