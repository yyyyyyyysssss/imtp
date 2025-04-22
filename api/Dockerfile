FROM ys0921/openjdk21:latest

MAINTAINER ys ysyanshuai2@gmail.com

WORKDIR /app

RUN chmod 777 /var/log

ADD target/imtp-web.jar /app/imtp-web.jar

CMD ["sh","-c","java -Xmx2G -Xms2G -XX:+UseZGC -XX:+ZGenerational -XX:+AlwaysPreTouch -Xlog:gc*:file=/var/log/java_gc.log -jar /app/imtp-web.jar"]