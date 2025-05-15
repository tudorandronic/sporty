package com.sportygroup.sporty.service;

import com.sportygroup.sporty.model.SportyEvent;
import com.sportygroup.sporty.repository.SportyEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SportyEventServiceTest {

    private SportyEventRepository sportyEventRepository;
    private SportyEventService sportyEventService;

    @BeforeEach
    void setUp() {
        sportyEventRepository = mock(SportyEventRepository.class);
        sportyEventService = new SportyEventService();
        sportyEventService.sportyEventRepository = sportyEventRepository;
    }

    @Test
    void processEventShouldUpdateStatusWhenEventExistsAndStatusChanged() {
        SportyEvent existingEvent = new SportyEvent();
        existingEvent.setEventId("event123");
        existingEvent.setStatus(false);

        SportyEvent incomingEvent = new SportyEvent();
        incomingEvent.setEventId("event123");
        incomingEvent.setStatus(true);

        when(sportyEventRepository.find("event123")).thenReturn(existingEvent);

        sportyEventService.processEvent(incomingEvent);

        assertTrue(existingEvent.isStatus(), "Status should be updated to true");
        verify(sportyEventRepository, never()).update(any());
    }

    @Test
    void processEventShouldCallUpdateWhenEventDoesNotExist() {
        SportyEvent incomingEvent = new SportyEvent();
        incomingEvent.setEventId("event456");
        incomingEvent.setStatus(true);

        when(sportyEventRepository.find("event456")).thenReturn(null);

        sportyEventService.processEvent(incomingEvent);

        verify(sportyEventRepository, times(1)).update(incomingEvent);
    }

    @Test
    void processEventShouldDoNothingWhenStatusIsSame() {
        SportyEvent existingEvent = new SportyEvent();
        existingEvent.setEventId("event789");
        existingEvent.setStatus(true);

        SportyEvent incomingEvent = new SportyEvent();
        incomingEvent.setEventId("event789");
        incomingEvent.setStatus(true);

        when(sportyEventRepository.find("event789")).thenReturn(existingEvent);

        sportyEventService.processEvent(incomingEvent);

        verify(sportyEventRepository, never()).update(any());
        assertTrue(existingEvent.isStatus(), "Status should remain unchanged");
    }
}

