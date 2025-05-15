package com.sportygroup.sporty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.sporty.model.SportyEvent;
import com.sportygroup.sporty.service.SportyEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SportyEventController.class)
public class SportyEventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private SportyEventService sportyEventService;

    @Autowired
    private ObjectMapper objectMapper;

    private SportyEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new SportyEvent();
        sampleEvent.setEventId("event123");
        sampleEvent.setStatus(true);

        doNothing().when(sportyEventService).processEvent(any(SportyEvent.class));
    }

    @Test
    void updateEventStatusWhenValidRequestShouldReturnOkAndEvent() throws Exception {
        String eventJson = objectMapper.writeValueAsString(sampleEvent);

        ResultActions resultActions = mockMvc.perform(post("/api/events/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId").value(sampleEvent.getEventId()))
                .andExpect(jsonPath("$.status").value(sampleEvent.isStatus()));

        verify(sportyEventService, times(1)).processEvent(any(SportyEvent.class));
    }
}