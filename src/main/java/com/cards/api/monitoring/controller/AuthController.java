package com.cards.api.monitoring.controller;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.cards.api.monitoring.config.AppConstants;
import com.cards.api.monitoring.config.CurrentUser;
import com.cards.api.monitoring.model.User;
import com.cards.api.monitoring.service.MailService;
import com.cards.api.monitoring.service.UserService;
import com.cards.api.monitoring.util.GeneralUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cards.api.monitoring.dto.ApiResponse;
import com.cards.api.monitoring.dto.JwtAuthenticationResponse;
import com.cards.api.monitoring.dto.LocalUser;
import com.cards.api.monitoring.dto.LoginRequest;
import com.cards.api.monitoring.dto.SignUpRequest;
import com.cards.api.monitoring.dto.SignUpResponse;
import com.cards.api.monitoring.exception.UserAlreadyExistAuthenticationException;
import com.cards.api.monitoring.security.jwt.TokenProvider;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserService userService;

	@Autowired
	TokenProvider tokenProvider;

	@Autowired
	private QrDataFactory qrDataFactory;

	@Autowired
	private QrGenerator qrGenerator;

	@Autowired
	private CodeVerifier verifier;

	@Autowired
	MailService mailService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		LocalUser localUser = (LocalUser) authentication.getPrincipal();
		boolean authenticated = !localUser.getUser().isUsing2FA();
		String jwt = tokenProvider.createToken(localUser, authenticated);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, authenticated, authenticated ? GeneralUtils.buildUserInfo(localUser) : null));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		try {
			User user = userService.registerNewUser(signUpRequest);
			final String token = UUID.randomUUID().toString();
			userService.createVerificationTokenForUser(user, token);
			mailService.sendVerificationToken(token, user);
			if (signUpRequest.isUsing2FA()) {
				QrData data = qrDataFactory.newBuilder().label(user.getEmail()).secret(user.getSecret()).issuer("Admin").build();
				// Generate the QR code image data as a base64 string which can
				// be used in an <img> tag:
				String qrCodeImage = getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
				return ResponseEntity.ok().body(new SignUpResponse(true, qrCodeImage));
			}
		} catch (UserAlreadyExistAuthenticationException e) {
			log.error("Exception Ocurred", e);
			return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
		} catch (QrGenerationException e) {
			log.error("QR Generation Exception Ocurred", e);
			return new ResponseEntity<>(new ApiResponse(false, "Unable to generate QR code!"), HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok().body(new ApiResponse(true, "User registered successfully"));
	}

	@PostMapping("/verify")
	@PreAuthorize("hasRole('PRE_VERIFICATION_USER')")
	public ResponseEntity<?> verifyCode(@NotEmpty @RequestBody String code, @CurrentUser LocalUser user) {
		if (!verifier.isValidCode(user.getUser().getSecret(), code)) {
			return new ResponseEntity<>(new ApiResponse(false, "Invalid Code!"), HttpStatus.BAD_REQUEST);
		}
		String jwt = tokenProvider.createToken(user, true);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, true, GeneralUtils.buildUserInfo(user)));
	}

	@PostMapping("/token/verify")
	public ResponseEntity<?> confirmRegistration(@NotEmpty @RequestBody String token) {
		final String result = userService.validateVerificationToken(token);
		return ResponseEntity.ok().body(new ApiResponse(true, result));
	}

	// user activation - verification
	@PostMapping("/token/resend")
	@ResponseBody
	public ResponseEntity<?> resendRegistrationToken(@NotEmpty @RequestBody String expiredToken) {
		if (!userService.resendVerificationToken(expiredToken)) {
			return new ResponseEntity<>(new ApiResponse(false, "Token not found!"), HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok().body(new ApiResponse(true, AppConstants.SUCCESS));
	}
}