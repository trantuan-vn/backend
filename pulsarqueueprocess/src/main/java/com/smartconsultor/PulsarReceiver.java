package com.smartconsultor;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.impl.auth.AuthenticationDisabled;
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData;
import org.apache.pulsar.spark.SparkStreamingPulsarReceiver;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

public class PulsarReceiver {
    public static void main(String[] args) {
        try {
            String serviceUrl = "pulsar://pulsar-broker:6650/";
            String adminUrl = "http://pulsar-broker:8080"; // URL của Pulsar Admin API

            // Tạo SparkConf và JavaStreamingContext
            SparkConf sparkConf = new SparkConf().setAppName("PulsarReceiver")
                                    // Cấu hình Predicate Pushdown
                                    .set("spark.sql.parquet.filterPushdown", "true")  
                                    .set("spark.sql.orc.filterPushdown", "true")
                                    .set("spark.sql.hive.metastorePartitionPruning", "true")
                                    // Cấu hình Parallel Execution
                                    .set("spark.sql.shuffle.partitions", "200")
                                    .set("spark.sql.files.maxPartitionBytes", "128MB");
        
            JavaStreamingContext jsc = new JavaStreamingContext(sparkConf, Durations.seconds(60));

            // Lấy danh sách topic từ namespace "public/default" sử dụng Pulsar Admin
            PulsarAdmin admin = PulsarAdmin.builder()
                    .serviceHttpUrl(adminUrl)
                    .authentication(new AuthenticationDisabled())
                    .build();

            List<String> topics = admin.topics().getList("public/default");

            // Đóng Pulsar Admin sau khi lấy danh sách topic
            admin.close();

            // Tạo luồng xử lý song song cho mỗi topic
            List<CompletableFuture<Void>> futures = topics.stream().map(topic -> CompletableFuture.runAsync(() -> {
                try {
                    // Sử dụng mỗi topic một subscription riêng biệt
                    String subscription = "subscription-" + topic.replaceAll("[/:]", "_");

                    ConsumerConfigurationData<byte[]> pulsarConf = new ConsumerConfigurationData<>();
                    pulsarConf.setTopicNames(Collections.singleton(topic));
                    pulsarConf.setSubscriptionName(subscription);

                    SparkStreamingPulsarReceiver pulsarReceiver = new SparkStreamingPulsarReceiver(
                            serviceUrl,
                            pulsarConf,
                            new AuthenticationDisabled());

                    JavaReceiverInputDStream<byte[]> lineDStream = jsc.receiverStream(pulsarReceiver);

                    JavaPairDStream<String, Integer> result = lineDStream.flatMap(x -> {
                                try {
                                    String line = new String(x, StandardCharsets.UTF_8);
                                    List<String> list = Arrays.asList(line.split(" "));
                                    return list.iterator();
                                } catch (Exception e) {
                                    System.err.println("Error processing message: " + e.getMessage());
                                    return Arrays.asList("").iterator();
                                }
                            })
                            .mapToPair(x -> new Tuple2<>(x, 1))
                            .reduceByKey(Integer::sum);

                    result.print();

                } catch (Exception e) {
                    System.err.println("Error processing topic " + topic + ": " + e.getMessage());
                }
            })).collect(Collectors.toList());

            // Đợi tất cả các luồng hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Đăng ký UncaughtExceptionHandler để xử lý lỗi toàn cục
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                System.err.println("Uncaught exception in thread " + thread.getName() + ": " + throwable.getMessage());
                jsc.stop(true, true);
            });

            jsc.start();
            jsc.awaitTermination();

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}
