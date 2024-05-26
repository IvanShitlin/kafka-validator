package com.shitlin.kafkavalidator.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitlin.kafkavalidator.configuration.Constant;
import com.shitlin.kafkavalidator.message.Message;
import com.shitlin.kafkavalidator.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTopicListener {

    public static final int MINIMUM_DATA_LENGTH = 10;
    public static final int MAX_HOURS_DIFFERENCE = 24;

    private final KafkaProducer kafkaProducer;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = Constant.INPUT_TOPIC, groupId = "kafka-validator", concurrency = "1", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(String message) {
        Optional.of(message)
                .map(this::deserializeMessage)
                .filter(this::filterByTimestamp)
                .map(this::processStatus)
                .ifPresent(kafkaProducer::sendMessage);
        }

    private Message deserializeMessage(String input) {
        try {
            return objectMapper.readValue(input, Message.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize message: {}. Skipping>>>", e.getMessage());
            return null;
        }
    }

    private boolean filterByTimestamp(Message message) {
        return Duration.between(message.timestamp(), Instant.now()).abs().toHours() <= MAX_HOURS_DIFFERENCE;
    }

    private  Message processStatus(Message message) {
        return message.data().length() > MINIMUM_DATA_LENGTH ? Message.valid(message) : Message.invalid(message);
    }

}
