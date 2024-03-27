FROM  maven:3.6.3-jdk-8-slim AS build
WORKDIR /srv

COPY . /srv
RUN cd /srv && mvn clean install -Dmanven.test.skip=true

FROM openjdk:10.0.2-jre-slim

# 设置时区
ENV TZ=Asia/Shanghai

RUN ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

EXPOSE 9090

COPY --from=build /srv/target/*.jar /srv/
ENTRYPOINT ["java", "-server", "-jar", "/srv/dingtalk-robot-demo-1.0-SNAPSHOT.jar"]