package com.cards.api.monitoring.controller;

import com.cards.api.monitoring.config.MockUserUtils;
import com.cards.api.monitoring.config.WithMockCustomUser;
import com.cards.api.monitoring.dto.LocalUser;
import com.cards.api.monitoring.dto.LoginRequest;
import com.cards.api.monitoring.model.User;
import com.cards.api.monitoring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.samstevens.totp.code.CodeVerifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private CodeVerifier verifier;

	@MockBean
	private AuthenticationManager authenticationManager;

	private static User user = MockUserUtils.getMockUser("Admin");

	private static ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testAuthenticateUser() throws Exception {
		LocalUser localUser = LocalUser.create(user, null, null, null);
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(localUser, null);
		Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
		String json = mapper.writeValueAsString(loginRequest);
		//mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				//.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("true")).andExpect(jsonPath("$.accessToken").isNotEmpty());

		// Test when user 2fa is enabled
		user.setUsing2FA(true);
	//	mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				//.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("false")).andExpect(jsonPath("$.user").doesNotExist());
	}

	@Test
	@WithMockCustomUser
	public void testVerifyCodeWhenCodeIsNotValid() throws Exception {
		Mockito.when(verifier.isValidCode(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		String json = mapper.writeValueAsString("443322");
		//mockMvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				//.andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value("false")).andExpect(jsonPath("$.message").value("Invalid Code!"));
	}

	@Test
	@WithMockCustomUser
	public void testVerifyCodeWhenCodeIsValid() throws Exception {
		Mockito.when(verifier.isValidCode(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		String json = mapper.writeValueAsString("443322");
		//mockMvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				//.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("true")).andExpect(jsonPath("$.accessToken").isNotEmpty())
				//.andExpect(jsonPath("$.user").exists());
	}
}
