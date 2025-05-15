package com.sportygroup.sporty.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessage {
    private String eventId;
    private Double score;
    private LocalDateTime timestamp;
}
