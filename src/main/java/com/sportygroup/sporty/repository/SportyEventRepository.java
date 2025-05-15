package com.sportygroup.sporty.repository;

import com.sportygroup.sporty.model.SportyEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created the SportyEventRepository just to keep things clean and SOLID
 */
@Slf4j
@Repository
public class SportyEventRepository {
    ConcurrentHashMap<String, SportyEvent> sportyEventRepository;

    @PostConstruct
    public void init(){
        sportyEventRepository = new ConcurrentHashMap<>();
    }

    public void update(SportyEvent sportyEvent){
        log.info("Added event with id = "+sportyEvent.getEventId()+" in repo");
        sportyEventRepository.put(sportyEvent.getEventId(), sportyEvent);
    }

    public SportyEvent find(String sportyEventId){
        return sportyEventRepository.get(sportyEventId);
    }

    public List<SportyEvent> findAll(){
        return sportyEventRepository.entrySet().
                stream().
                map(se -> se.getValue()).
                collect(Collectors.toList());
    }
}
