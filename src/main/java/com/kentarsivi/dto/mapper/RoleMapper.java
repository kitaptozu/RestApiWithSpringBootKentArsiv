package com.kentarsivi.dto.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.kentarsivi.dto.role.RoleDto;
import com.kentarsivi.model.Role;

public class RoleMapper {

	public static RoleDto toRoleDto(Role role) {

		RoleDto roleDto = new RoleDto();

		roleDto.setId(role.getId());
		roleDto.setRole(role.getRole());
		roleDto.setUsers(null);

		return roleDto;
	}

	public static RoleDto toRoleDto(Optional<Role> role) {

		RoleDto roleDto = new RoleDto();
		roleDto.setId(role.get().getId());
		roleDto.setRole(role.get().getRole());
		roleDto.setUsers(null);

		return roleDto;
	}

	public static Role toRole(RoleDto roleDto) {

		Role role = new Role();

		role.setId(roleDto.getId());
		role.setRole(roleDto.getRole());
		role.setUsers(null);

		return role;
	}

	public static Set<RoleDto> toSetOfRoleDto(Set<Role> roles) {

		Set<RoleDto> roleDtos = new HashSet<RoleDto>();

		for (Role role : roles) {

			RoleDto roleDto = toRoleDto(role);

			roleDtos.add(roleDto);

		}
		return roleDtos;

	}

	public static List<RoleDto> toListOfRoleDto(Page<Role> roles) {

		List<RoleDto> roleDtos = new ArrayList<>();

		for (Role role : roles) {

			RoleDto roleDto = toRoleDto(role);

			roleDtos.add(roleDto);
		}

		return roleDtos;
	}

	public static Set<Role> toSetOfRole(Set<RoleDto> roleDtos) {

		Set<Role> roles = new HashSet<Role>();

		for (RoleDto roleDto : roleDtos) {

			Role role = toRole(roleDto);

			roles.add(role);

		}
		return roles;

	}

}
