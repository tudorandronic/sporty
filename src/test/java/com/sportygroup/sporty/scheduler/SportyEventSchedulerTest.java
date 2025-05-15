package com.sportygroup.sporty.scheduler;

import com.sportygroup.sporty.dto.ExternalServiceResponseDto;
import com.sportygroup.sporty.kafka.service.KafkaMessagePublisher;
import com.sportygroup.sporty.model.SportyEvent;
import com.sportygroup.sporty.repository.SportyEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

import static org.mockito.Mockito.*;

class SportyEventSchedulerTest {

    @InjectMocks
    private SportyEventScheduler scheduler;

    @Mock
    private SportyEventRepository sportyEventRepository;

    @Mock
    private KafkaMessagePublisher kafkaMessagePublisher;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler.restTemplate = restTemplate;
    }

    @Test
    void callApiAndSendMessageShouldPublishMessageForActiveEvents() {
        SportyEvent activeEvent = new SportyEvent();
        activeEvent.setEventId("event123");
        activeEvent.setStatus(true);

        ExternalServiceResponseDto responseDto = new ExternalServiceResponseDto();
        responseDto.setEventId("event123");
        responseDto.setCurrentScore(Double.valueOf(0.0));

        when(sportyEventRepository.findAll()).thenReturn(List.of(activeEvent));
        when(restTemplate.getForObject(any(), eq(ExternalServiceResponseDto.class), anyString()))
                .thenReturn(responseDto);

        scheduler.callApiAndSendMessage();

        verify(kafkaMessagePublisher, times(1)).publishScore(argThat(message ->
                message.getEventId().equals("event123") &&
                        message.getScore().equals(0.0)
        ));
    }

    @Test
    void callApiAndSendMessageShouldNotPublishIfExternalServiceFails() {

        SportyEvent activeEvent = new SportyEvent();
        activeEvent.setEventId("event456");
        activeEvent.setStatus(true);

        when(sportyEventRepository.findAll()).thenReturn(List.of(activeEvent));
        when(restTemplate.getForObject(anyString(), eq(ExternalServiceResponseDto.class), anyString()))
                .thenThrow(new RuntimeException("Service unavailable"));

        scheduler.callApiAndSendMessage();

        verify(kafkaMessagePublisher, never()).publishScore(any());
    }

    @Test
    void callApiAndSendMessageShouldIgnoreInactiveEvents() {
        SportyEvent inactiveEvent = new SportyEvent();
        inactiveEvent.setEventId("event789");
        inactiveEvent.setStatus(false);

        when(sportyEventRepository.findAll()).thenReturn(List.of(inactiveEvent));

        scheduler.callApiAndSendMessage();

        verify(restTemplate, never()).getForObject(anyString(), any(), anyString());
        verify(kafkaMessagePublisher, never()).publishScore(any());
    }
}

