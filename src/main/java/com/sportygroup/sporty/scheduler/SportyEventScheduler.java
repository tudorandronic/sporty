package com.sportygroup.sporty.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sportygroup.sporty.dto.ExternalServiceResponseDto;
import com.sportygroup.sporty.kafka.payload.KafkaMessage;
import com.sportygroup.sporty.kafka.service.KafkaMessagePublisher;
import com.sportygroup.sporty.model.SportyEvent;
import com.sportygroup.sporty.repository.SportyEventRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SportyEventScheduler {

    @Autowired
    private SportyEventRepository sportyEventRepository;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    @Autowired
    KafkaMessagePublisher kafkaMessagePublisher;

    RestTemplate restTemplate;

    @PostConstruct
    public void init(){
        restTemplate = new RestTemplate();
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void callApiAndSendMessage() {
        log.info("Started processing the live events at "+LocalDateTime.now());
        List<SportyEvent> activeSportyEvents = sportyEventRepository.
                findAll().
                stream().
                filter(s -> s.isStatus()).
                collect(Collectors.toList());

        for(SportyEvent sportyEvent:activeSportyEvents){
            ExternalServiceResponseDto externalServiceResponseDto = callExternalService(sportyEvent);
            if(externalServiceResponseDto != null){
                kafkaMessagePublisher.publishScore(KafkaMessage.
                        builder().
                        eventId(externalServiceResponseDto.getEventId()).
                        score(externalServiceResponseDto.getCurrentScore()).
                        timestamp(LocalDateTime.now()).build()
                );
            }
        }
        log.info("Finished processing the live events at "+ LocalDateTime.now());
    }

    public ExternalServiceResponseDto callExternalService(SportyEvent sportyEvent){
        log.info("Starting rest call for id: "+sportyEvent.getEventId());
        ExternalServiceResponseDto sportyRestApiResponse = null;
        try{
            sportyRestApiResponse = restTemplate.getForObject(externalServiceUrl, ExternalServiceResponseDto.class, sportyEvent.getEventId());
        } catch (RestClientException restClientException) {
            log.info("Error occured when calling the external service for id: "+sportyEvent.getEventId()+
                    " with error: "+
                    restClientException.getMessage());
        }
        return sportyRestApiResponse;
    }
}
