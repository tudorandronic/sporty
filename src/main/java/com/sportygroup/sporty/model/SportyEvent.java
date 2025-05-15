package com.sportygroup.sporty.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main model for our service, used to map the controller request
 * (should have been a dto but we have a time contraint)
 * to control the execution of the scheduler(live/not live)
 * and to be used by the repository
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SportyEvent {
    String eventId;
    boolean status;
}
