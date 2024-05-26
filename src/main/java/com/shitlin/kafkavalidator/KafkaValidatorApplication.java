package com.shitlin.kafkavalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class KafkaValidatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaValidatorApplication.class, args);
    }

}
