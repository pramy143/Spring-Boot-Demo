package com.cards.api.monitoring.config;

import com.cards.api.monitoring.dto.LocalUser;
import com.cards.api.monitoring.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		User user = MockUserUtils.getMockUser(customUser.username());
		LocalUser localUser = LocalUser.create(user, null, null, null);
		Authentication auth = new UsernamePasswordAuthenticationToken(localUser, user.getPassword(), localUser.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}