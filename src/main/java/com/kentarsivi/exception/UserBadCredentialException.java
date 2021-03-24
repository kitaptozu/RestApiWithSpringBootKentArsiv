package com.kentarsivi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserBadCredentialException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UserBadCredentialException(String message) {
		super(message);
	}
}
