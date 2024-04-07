package com.matuzas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class Launcher {

    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static final LifecycleManager LIFECYCLE_MANAGER = new LifecycleManager();

    private static final Lifecycle KAFKA_CONSUMERS = new KafkaConsumerManager();

    public static void main(String[] args) {
        LIFECYCLE_MANAGER.add(KAFKA_CONSUMERS);

        try {
            // Start
            log.info("Starting application using Java {}", getJavaRuntimeVersion());
            LIFECYCLE_MANAGER.startAll();
            addShutdownHook();

            // Started successfully
            log.info("Application started successfully");

            // Wait until stopped
            shutdownLatch.await();

            // Stopped successfully
            log.info("Stopped application");
        } catch (Exception ex) {
            log.error("Failed to start application", ex);
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Shutdown signal received");
                LIFECYCLE_MANAGER.stopAll();
                shutdownLatch.countDown();
            } catch (Exception ex) {
                log.error("Error during shutdown: {}", ex.getMessage());
            }
        }));
    }

    private static String getJavaRuntimeVersion() {
        return System.getProperty("java.runtime.version");
    }
}