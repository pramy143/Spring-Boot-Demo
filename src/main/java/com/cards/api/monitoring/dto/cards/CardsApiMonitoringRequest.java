package com.cards.api.monitoring.dto.cards;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class CardsApiMonitoringRequest {

    @NotBlank
    private String apiName;
    @NotBlank
    private String apiServiceURL;
    @NotBlank
    private String statusAsOn;
    @NotBlank
    private String status;
}
