package com.sportygroup.sporty.kafka.service;

import com.sportygroup.sporty.kafka.payload.KafkaMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

/**
 * Generated with AI due to time constraints
 */
class KafkaMessagePublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaMessagePublisher kafkaMessagePublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaMessagePublisher = new KafkaMessagePublisher(kafkaTemplate, "test-topic");
    }

    @Test
    void testPublishScore_validMessage_sendsToKafka() {
        KafkaMessage message = KafkaMessage.builder()
                .eventId("1")
                .score(9.5)
                .timestamp(LocalDateTime.now())
                .build();

        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        kafkaMessagePublisher.publishScore(message);

        verify(kafkaTemplate, times(1)).send(eq("test-topic"), eq("1"), contains("\"score\":9.5"));
    }

    @Test
    void testPublishScore_nullMessage_doesNotSend() {
        kafkaMessagePublisher.publishScore(null);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void testPublishScore_invalidEventId_doesNotSend() {
        KafkaMessage message = KafkaMessage.builder()
                .eventId("")
                .score(7.0)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaMessagePublisher.publishScore(message);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}

