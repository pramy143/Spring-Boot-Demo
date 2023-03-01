package com.cards.api.monitoring.service.cards;

import com.cards.api.monitoring.dto.cards.CardsApiMonitoringRequest;
import com.cards.api.monitoring.exception.ResourceNotFoundException;
import com.cards.api.monitoring.model.Cards;
import com.cards.api.monitoring.repo.CardsApiHealthCheckRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.cards.api.monitoring.config.AppConstants.ID;

;

@Service
public class CardsApiHealthCheckServiceImpl implements CardsApiHealthCheckService {
 
    @Autowired
    private CardsApiHealthCheckRepository cardsApiHealthCheckRepository;
 
    @Override
    public Cards save(CardsApiMonitoringRequest cardsApiMonitoringRequest) {
        Cards cardsApiServiceList = new Cards();
        BeanUtils.copyProperties(cardsApiMonitoringRequest, cardsApiServiceList);
        return cardsApiHealthCheckRepository.save(cardsApiServiceList);
    }
 
    @Override
    public void deleteById(Long id) {
        cardsApiHealthCheckRepository.deleteById(id);
    }
 
    @Override
    public Optional<Cards> findById(Long id) {
        return cardsApiHealthCheckRepository.findById(id);
    }
 
    @Override
    public Page<Cards> findAll(Pageable pageable) {
        return cardsApiHealthCheckRepository.findAll(pageable);
    }
 
    @Override
    public void update(Long id, CardsApiMonitoringRequest cardsApiMonitoringRequest) {
        findById(id).map(cardsApiServices -> {
            BeanUtils.copyProperties(cardsApiMonitoringRequest, cardsApiServices);
            return cardsApiHealthCheckRepository.save(cardsApiServices);
        }).orElseThrow(() -> new ResourceNotFoundException("Cards", ID, id));
    }
}