package com.kentarsivi.dto.user;

import java.util.HashSet;
import java.util.Set;

import com.kentarsivi.dto.role.RoleDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserDto {

	private Long id;

	private String firstName;

	private String lastName;

	private String username;

	private String password;

	private Boolean state;

	private Set<RoleDto> roles = new HashSet<>();

}
