package com.cards.api.monitoring.dto;

import lombok.Value;

@Value
public class ApiResponse {
	private Boolean success;
	private String message;
}