FROM openjdk:11.0.13-jdk
COPY ./target/jellyfin_pinyin.jar /app/
ENV TZ "Asia/Shanghai"
ENV TIME 3600
ENV DOMAIN "http://127.0.0.1:8096"
ENV KEY ""
ENV MEDIA ""

WORKDIR /app
CMD ["java","-jar","/app/jellyfin_pinyin.jar"]
