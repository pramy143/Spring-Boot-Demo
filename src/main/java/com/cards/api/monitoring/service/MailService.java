package com.cards.api.monitoring.service;

import com.cards.api.monitoring.model.User;

public interface MailService {

	void sendVerificationToken(String token, User user);
}
