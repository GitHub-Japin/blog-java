FROM java:8
MAINTAINER Japin<1940005020@qq.com>
# 服务器只有dockerfile和jar在同级目录
COPY *.jar /app.jar
CMD ["--server.port=8081"]
# 指定容器内要暴露的端口
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]
