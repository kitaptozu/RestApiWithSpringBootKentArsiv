package com.kentarsivi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticationResponseDto {

	private final String jwt;
}
