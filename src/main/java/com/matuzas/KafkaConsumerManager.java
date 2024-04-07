package com.matuzas;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class KafkaConsumerManager implements Lifecycle {

    private final Set<String> processedMessages = ConcurrentHashMap.newKeySet();

    private final String TOPIC = "commands";
    private final Properties properties;

    private ExecutorService executorService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public KafkaConsumerManager() {
        properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092;127.0.0.1:9093;127.0.0.1:9094");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "commands-consumer");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE.toString());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @Override
    public void start() throws Exception {
        log.info("Starting Kafka Consumers");

        if (running.compareAndSet(false, true)) {
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                try (var consumer = new KafkaConsumer<String, String>(properties)) {
                    consumer.subscribe(List.of(TOPIC));
                    while (running.get()) {
                        var records = consumer.poll(Duration.ofMillis(100));
                        for (var record : records) {
                            var key = record.key();
                            var isNotProcessed = !processedMessages.contains(key);
                            if (isNotProcessed) {
                                log.info("Consumed record with key {} and value: {}", key, record.value());
                                processedMessages.add(key);
                                // Process
                            } else {
                                log.warn("Record with key {} is already processed", key);
                            }

                            // Commit offsets
                            var topicPartition = new TopicPartition(record.topic(), record.partition());
                            var offsets = Map.of(topicPartition, new OffsetAndMetadata(record.offset() + 1));

                            log.info("Commiting offsets: {}", offsets);

                            consumer.commitSync(offsets);
                        }
                    }
                } catch (Exception ex) {
                    running.set(false);
                    throw ex;
                }
            });

            log.info("Kafka consumer started for topic: {}", TOPIC);
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping Kafka Consumers");
        if (running.compareAndSet(true, false)) {
            executorService.shutdown();
            log.info("Kafka Consumers stopped");
        }
    }
}
