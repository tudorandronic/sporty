package com.sportygroup.sporty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServiceResponseDto {
    private String eventId;
    private Double currentScore;
}
