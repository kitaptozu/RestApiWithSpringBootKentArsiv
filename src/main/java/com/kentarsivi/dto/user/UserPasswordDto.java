package com.kentarsivi.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordDto {

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 8)
	private String currentPassword;
	
	
	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 8)
	private String password;
	

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 8)
	private String confirmPassword;

}
