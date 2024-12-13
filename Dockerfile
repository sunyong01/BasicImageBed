FROM alpine/java:21-jre

# 设置工作目录
WORKDIR /app

# 创建数据目录
RUN mkdir -p /app/data

# 复制jar文件到容器中
COPY app.jar /app/

# 设置时区
ENV TZ=Asia/Shanghai

# 设置默认的JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 