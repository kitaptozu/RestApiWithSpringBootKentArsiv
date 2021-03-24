package com.kentarsivi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class FolderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FolderNotFoundException(String message) {
		super(message);
	}
}
