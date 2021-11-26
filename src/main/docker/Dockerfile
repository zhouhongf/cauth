FROM anapsix/alpine-java
VOLUME /tmp
ADD cauth-0.0.1.jar app.jar
EXPOSE 9999
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
