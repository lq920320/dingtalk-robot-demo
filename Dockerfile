FROM  maven:3.6.3-jdk-8-slim as build
WORKDIR /srv

COPY . /srv
RUN cd /srv && mvn clean install -Dmanven.test.skip=true

# 定义时区参数
ENV TZ=Asia/Shanghai \
    DEBIAN_FRONTEND=noninteractive

# 设置时区
RUN ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && dpkg-reconfigure --frontend noninteractive tzdata \
    && rm -rf /var/lib/apt/lists/*

FROM openjdk:10.0.2-jre-slim

EXPOSE 9090

COPY --from=build /srv/target/*.jar /srv/
ENTRYPOINT ["java", "-server", "-jar", "/srv/dingtalk-robot-demo-1.0-SNAPSHOT.jar"]