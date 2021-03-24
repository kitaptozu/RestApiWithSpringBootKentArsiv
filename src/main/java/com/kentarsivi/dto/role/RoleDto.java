package com.kentarsivi.dto.role;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.kentarsivi.dto.user.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {

	private Long id;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	private String role;

	private Set<UserDto> users;

}
