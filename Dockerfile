FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/river-chat.jar /river-chat/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/river-chat/app.jar"]
