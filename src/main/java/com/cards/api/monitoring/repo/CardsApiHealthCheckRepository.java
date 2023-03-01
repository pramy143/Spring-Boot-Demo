package com.cards.api.monitoring.repo;

import com.cards.api.monitoring.model.Cards;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardsApiHealthCheckRepository extends PagingAndSortingRepository<Cards, Long> {
 
}