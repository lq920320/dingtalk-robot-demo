FROM  maven:3.8.5-openjdk-17-slim AS build
WORKDIR /srv

RUN ls
RUN pwd

COPY settings.xml /etc/maven/settings.xml
COPY . /srv
RUN cd /srv && mvn clean install -Dmanven.test.skip=true

FROM openjdk:17.0.2-jdk-slim-buster

# 设置时区
ENV TZ=Asia/Shanghai

RUN ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

EXPOSE 9090

COPY --from=build /srv/target/*.jar /srv/
ENTRYPOINT ["java", "-server", "-jar", "/srv/dingtalk-robot-demo-1.0-SNAPSHOT.jar"]