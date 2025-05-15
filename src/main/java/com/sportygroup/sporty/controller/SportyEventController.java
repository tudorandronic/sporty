package com.sportygroup.sporty.controller;

import com.sportygroup.sporty.model.SportyEvent;
import com.sportygroup.sporty.service.SportyEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class SportyEventController {

    @Autowired
    SportyEventService sportyEventService;

    @PostMapping(
            value = "/events/status",
            produces = "application/json"
    )
    public ResponseEntity<SportyEvent> updateEventStatus(@RequestBody SportyEvent sportyEvent){
        log.info("Received the following event ("+sportyEvent.getEventId()+","+sportyEvent.isStatus()+")");
        sportyEventService.processEvent(sportyEvent);
        return ResponseEntity.ok().body(sportyEvent);
    }
}
