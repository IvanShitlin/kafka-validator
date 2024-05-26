package com.shitlin.kafkavalidator.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitlin.kafkavalidator.configuration.Constant;
import com.shitlin.kafkavalidator.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Transactional
    public void sendMessage(Message message) {
        Optional.of(message)
                .map(this::serializeMessageToJson)
                .ifPresent(this::send);
    }

    private void send(String message) {
        kafkaTemplate.send(Constant.OUTPUT_TOPIC, message);
    }

    private String serializeMessageToJson(Message message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize message: {}. Error: {}", message, e.getMessage());
            return null;
        }
    }

}
