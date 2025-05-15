package com.sportygroup.sporty.service;

import com.sportygroup.sporty.model.SportyEvent;
import com.sportygroup.sporty.repository.SportyEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SportyEventService {

    @Autowired
    SportyEventRepository sportyEventRepository;

    public void processEvent(SportyEvent event){
        SportyEvent sportyEvent = sportyEventRepository.find(event.getEventId());
        if(sportyEvent != null && event.isStatus() != sportyEvent.isStatus()){
            sportyEvent.setStatus(event.isStatus());
        } else if(sportyEvent == null) {
            sportyEventRepository.update(event);
        }
    }
}
