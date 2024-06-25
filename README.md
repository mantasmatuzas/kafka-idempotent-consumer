# Kafka Idempotent Consumer

## Introduction

The Kafka Idempotent Consumer project offers a robust solution for consuming messages from Kafka topics idempotently,
ensuring that messages are processed exactly once. This project is particularly useful for scenarios where duplicate
processing of messages is unacceptable, such as in financial transactions or event logging. It leverages the
native `kafka-clients` library, avoiding the complexity of integrating with major frameworks.

## Key Features

- **Framework-Independent**: Utilizes `kafka-clients` for a lightweight, framework-independent implementation.
- **Lifecycle Management**: Features a `Lifecycle` interface for graceful startup and shutdown of system components.
- **Manual Offset Committing**: Employs manual offset committing post-message processing for precise consumption
  control.
- **Idempotency Assurance**: Tracks processed messages using an internal data structure to prevent duplicates.

## Quick Start with Docker Compose

To simplify the development and testing process, we've included a Docker Compose setup that spins up a three-node Kafka
cluster with a single Zookeeper instance and Kafka-UI for easy monitoring. Kafka-UI will be accessible
on `localhost:8080`.

### Prerequisites

- Docker and Docker Compose installed on your machine.

### Running the Kafka Cluster

1. **Navigate to the Project Directory**: Change into the project directory where the `docker-compose.yml` file is
   located.

    ```bash
    cd kafka-idempotent-consumer
    ```

2. **Start the Kafka Cluster**: Use Docker Compose to start the services defined in `docker-compose.yml`.

    ```bash
    docker-compose up -d
    ```

   This command starts the Kafka cluster, Zookeeper, and Kafka-UI as background services.

3. **Access Kafka-UI**: Open your web browser and go to `http://localhost:8080` to access Kafka-UI, where you can
   monitor your Kafka cluster and topics.

4. **Shutting Down**: To stop and remove the containers, networks, and volumes created by Docker Compose, run:

    ```bash
    docker-compose down
    ```