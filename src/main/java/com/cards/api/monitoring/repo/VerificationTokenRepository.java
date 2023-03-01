package com.cards.api.monitoring.repo;

import com.cards.api.monitoring.model.User;
import com.cards.api.monitoring.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	VerificationToken findByToken(String token);

	VerificationToken findByUser(User user);
}
