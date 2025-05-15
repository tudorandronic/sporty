package com.sportygroup.sporty.mock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * For speedyness let's consider this controller the external service that responds
 * to our rest queries which are done every 10 seconds.
 */
@Slf4j
@RestController
@RequestMapping("/sporty")
public class ExternalMockService {

    @GetMapping(
            value = "/score",
            produces = "application/json"
    )
    public ResponseEntity<MockResponse> updateEventStatus(@RequestParam("id") String id){
        MockResponse mockResponse = new MockResponse(id,generateRandomScore());
        return ResponseEntity.ok().body(mockResponse);
    }

    private Double generateRandomScore(){
        Random random = new Random();
        return 100 * random.nextDouble();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MockResponse {
        private String eventId;
        private Double currentScore;
    }
}
