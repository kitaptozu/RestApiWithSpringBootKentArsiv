package com.kentarsivi.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserCreationDto {

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(max = 60)
	private String firstName;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(max = 60)
	private String lastName;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(max = 30)
	private String username;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 8)
	private String password;

}
