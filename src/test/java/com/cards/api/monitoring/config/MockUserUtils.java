package com.cards.api.monitoring.config;

import java.util.Arrays;
import java.util.HashSet;

import com.cards.api.monitoring.model.Role;
import com.cards.api.monitoring.model.User;

public class MockUserUtils {

	private MockUserUtils() {
	}
	/**
	 * 
	 */
	public static User getMockUser(String username) {
		User user = new User();
		user.setId(1L);
		user.setEmail(username);
		user.setPassword("secret");
		Role role = new Role();
		role.setName(Role.ROLE_PRE_VERIFICATION_USER);
		user.setRoles(new HashSet<>(Arrays.asList(role)));
		user.setEnabled(true);
		user.setSecret("secret");
		return user;
	}
}
