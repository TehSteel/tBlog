FROM openjdk:17-alpine

ENV APP_FILE tblog.jar
ENV APP_HOME /app

EXPOSE 8088

COPY target/$APP_FILE $APP_HOME/
WORKDIR $APP_HOME

CMD ["java", "-jar", "tblog.jar"]
