# Sử dụng image Java từ OpenJDK
FROM openjdk:11-jre-slim

# Thêm thư viện Pulsar
RUN apt-get update && apt-get install -y wget && \
    wget https://archive.apache.org/dist/pulsar/pulsar-2.10.2/apache-pulsar-2.10.2-bin.tar.gz && \
    tar -xvzf apache-pulsar-2.10.2-bin.tar.gz && \
    mv apache-pulsar-2.10.2 /pulsar

# Copy jar Pulsar Function đã build vào container
COPY target/vertx-pulsar-function-1.0-jar-with-dependencies.jar /usr/local/app/

# Set environment variables
ENV PULSAR_HOME=/pulsar
ENV PATH=$PATH:$PULSAR_HOME/bin

# Expose port cho Pulsar Function
EXPOSE 8080

# Lệnh chạy Pulsar Function
CMD ["pulsar-admin", "functions", "create", "--jar", "/usr/local/app/vertx-pulsar-function-1.0-jar-with-dependencies.jar", "--classname", "com.example.function.VertxOrderFunction", "--name", "vertx-order-db-fn", "--inputs", "orders-topic", "--output", "results-topic", "--parallelism", "3"]
