package com.smartconsultor.microservice.business;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.smartconsultor.microservice.business.adapter.dto.gateway.GatewayMessage;
import com.smartconsultor.microservice.business.adapter.flink.process.TransactionProcessFunction;
import com.smartconsultor.microservice.business.application.config.AppConfig;
import com.smartconsultor.microservice.business.application.config.PulsarConfig;
import com.smartconsultor.microservice.business.di.component.FlinkJobComponent;
import com.smartconsultor.microservice.business.domain.model.TransactionResult;
import com.smartconsultor.microservice.business.di.component.DaggerFlinkJobComponent;

import javax.inject.Inject;


public class FlinkJobRunner {

    @Inject
    TransactionProcessFunction transactionProcessFunction;

    @Inject
    DeserializationSchema<GatewayMessage> gatewayMessageDeserialization;

    @Inject
    SerializationSchema<TransactionResult> resultMessageSerialization;

    @Inject
    AppConfig appConfig;

    public static void main(String[] args) throws Exception {
        FlinkJobRunner runner = new FlinkJobRunner();
        runner.initDI();
        runner.run();
    }

    private void initDI() {
        FlinkJobComponent component = DaggerFlinkJobComponent.create();
        component.inject(this);
    }

    public void run() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(40);
        env.enableCheckpointing(30000); // checkpoint mỗi 30s
        env.getConfig().setAutoWatermarkInterval(1000);

        PulsarConfig pulsarConfig = appConfig.getPulsarConfig();

        PulsarSource<GatewayMessage> pulsarSource = PulsarSource.builder()
            .setServiceUrl(pulsarConfig.getServiceUrl())
            .setAdminUrl(pulsarConfig.getAdminUrl())
            .setTopicPattern("persistent://public/default/" + pulsarConfig.getTopicIn() + "-.*")
            .setStartCursor(StartCursor.latest())
            .setDeserializationSchema(gatewayMessageDeserialization)
            .build();

        PulsarSink<TransactionResult> pulsarSink = PulsarSink.builder()
            .setServiceUrl(pulsarConfig.getServiceUrl())
            .setAdminUrl(pulsarConfig.getAdminUrl())
            .setTopics("persistent://public/default/" + pulsarConfig.getTopicOut())
            .setSerializationSchema(resultMessageSerialization)
            .build();
            

        env.fromSource(pulsarSource, WatermarkStrategy.noWatermarks(), "pulsar-source")
           .keyBy(GatewayMessage::getUserId)
           .process(transactionProcessFunction)
           .sinkTo(pulsarSink);

        env.execute("E-Wallet Flink Job");
    }
}
