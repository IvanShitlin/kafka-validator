package com.shitlin.kafkavalidator.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Message(
        String id,
        @JsonProperty("timestamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
        Instant timestamp,
        String data,
        String status
) {
        public static Message valid(Message from) {
                return new Message(from.id, from.timestamp, from.data, "valid");
        }

        public static Message invalid(Message from) {
                return new Message(from.id, from.timestamp, from.data, "invalid");
        }


}
