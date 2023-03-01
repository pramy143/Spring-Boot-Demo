package com.cards.api.monitoring.controller;

import com.cards.api.monitoring.config.MockUserUtils;
import com.cards.api.monitoring.dto.LocalUser;
import com.cards.api.monitoring.dto.LoginRequest;
import com.cards.api.monitoring.model.User;
import com.cards.api.monitoring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samstevens.totp.code.CodeVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class AuthControllerTest2 {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private CodeVerifier verifier;

	@MockBean
	private PasswordEncoder passwordEncoder;

	private static User user = MockUserUtils.getMockUser("Admin");

	private static ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	public void init() {
		LocalUser localUser = LocalUser.create(user, null, null, null);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(localUser, "secret"));
		Mockito.when(userService.findUserByEmail(Mockito.anyString())).thenReturn(user);
		Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
	}

	@Test
	public void testAuthenticateUser() throws Exception {
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
		String json = mapper.writeValueAsString(loginRequest);
		mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("true")).andExpect(jsonPath("$.accessToken").isNotEmpty());

		// Test when user 2fa is enabled
		user.setUsing2FA(true);
		mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("false")).andExpect(jsonPath("$.user").doesNotExist());
	}

	@Test
	public void testVerifyCodeWhenCodeIsNotValid() throws Exception {
		Mockito.when(verifier.isValidCode(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		String json = mapper.writeValueAsString("443322");
		mockMvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value("false")).andExpect(jsonPath("$.message").value("Invalid Code!"));
	}

	@Test
	public void testVerifyCodeWhenCodeIsValid() throws Exception {
		Mockito.when(verifier.isValidCode(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		String json = mapper.writeValueAsString("443322");
		mockMvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("true")).andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.user").exists());
	}
}
