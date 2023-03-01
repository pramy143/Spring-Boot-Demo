package com.cards.api.monitoring.controller;

import static com.cards.api.monitoring.config.AppConstants.ID;
 
import javax.validation.Valid;

import com.cards.api.monitoring.dto.ApiResponse;
import com.cards.api.monitoring.dto.cards.CardsApiMonitoringRequest;
import com.cards.api.monitoring.model.Cards;
import com.cards.api.monitoring.service.cards.CardsApiHealthCheckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.cards.api.monitoring.config.AppConstants;
import com.cards.api.monitoring.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping(path = "/api/cards-healthcheck")
@RequiredArgsConstructor
public class CardsServiceMonitoringController {
 
    private final CardsApiHealthCheckService cardsApiHealthCheckService;
 
    @GetMapping
    public Page<Cards> getCardsApiServiceList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        Pageable paging = PageRequest.of(page, size);
        return cardsApiHealthCheckService.findAll(paging);
    }
 
    @GetMapping("/{id}")
    public Cards getCardsApiService(@PathVariable Long id) {
        return cardsApiHealthCheckService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cards", ID, id));
    }
 
    @PostMapping
    public ResponseEntity<?> createCardsApiService(@Valid @RequestBody CardsApiMonitoringRequest cardsApi) {
        cardsApiHealthCheckService.save(cardsApi);
        return ResponseEntity.ok().body(new ApiResponse(true, AppConstants.SUCCESS));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCardsApiService(@PathVariable Long id, @Valid @RequestBody CardsApiMonitoringRequest cardsApiRequest) {
        cardsApiHealthCheckService.update(id, cardsApiRequest);
        return ResponseEntity.ok().body(new ApiResponse(true, AppConstants.SUCCESS));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCardsApiService(@PathVariable Long id) {
        return cardsApiHealthCheckService.findById(id).map(p -> {
            cardsApiHealthCheckService.deleteById(id);
            return ResponseEntity.ok().body(new ApiResponse(true, AppConstants.SUCCESS));
        }).orElseThrow(() -> new ResourceNotFoundException("Cards", ID, id));
    }
}