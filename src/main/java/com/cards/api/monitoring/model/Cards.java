package com.cards.api.monitoring.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cards_api_services")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Cards extends BaseEntity {
 
    /**
     * 
     */
    private static final long serialVersionUID = 1493867573442690205L;
    @NonNull
    private String apiName;
    @NonNull
    private String apiServiceURL;
    @NonNull
    private String statusAsOn;
    @NonNull
    private String status;

}