package com.sportygroup.sporty.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sportygroup.sporty.kafka.payload.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Generated with AI for speed but modified by me as ChatGPT used ListableFutures
 * which are long dead and burried.
 * This class uses KafkaTemplate to send a KafkaMessage to the event-scores topic.
 * In turn we get a CompletableFuture (much better !) which we can use to see if
 * the messages has been successfully sent to the topic.
 * I love CompletableFutures. Don't you ? :)
 */
@Component
@Slf4j
public class KafkaMessagePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;
    private ObjectMapper objectMapper;

    @Autowired
    public KafkaMessagePublisher(KafkaTemplate<String, String> kafkaTemplate,
                                 @Value("${app.kafka.topic:event-scores-topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void publishScore(KafkaMessage message) {
        if (message == null || message.getEventId() == null || message.getEventId().isEmpty()) {
            log.warn("Attempted to publish a null or invalid message: {}", message);
            return;
        }

        log.info("Attempting to publish message for eventId {}: {}", message.getEventId(), message);
        String json = null;
        try{
            json = objectMapper.writeValueAsString(message);
        } catch(JsonProcessingException processingException){
            log.error("Json processing failed : "+processingException.getMessage());
        }

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, message.getEventId(), json);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published message for eventId {}: to topic {} partition {} offset {}",
                        message.getEventId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish message for eventId {}: {}", message.getEventId(), ex.getMessage(), ex);
            }
        });
    }
}

