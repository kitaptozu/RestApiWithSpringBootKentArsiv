package com.kentarsivi.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kentarsivi.dto.user.UserCreationByAdminDto;
import com.kentarsivi.dto.user.UserCreationDto;
import com.kentarsivi.dto.user.UserDto;
import com.kentarsivi.dto.user.UserPasswordDto;
import com.kentarsivi.dto.user.UserUpdateByAdminDto;
import com.kentarsivi.model.User;

public class UserMapper {

	public static UserDto toUserDto(User user) {

		UserDto userDto = new UserDto();

		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setPassword(user.getPassword());
		userDto.setState(user.getState());

		userDto.setRoles(RoleMapper.toSetOfRoleDto(user.getRoles()));

		return userDto;
	}

	public static UserDto toUserDto(Optional<User> user) {

		UserDto userDto = new UserDto();

		userDto.setId(user.get().getId());
		userDto.setUsername(user.get().getUsername());
		userDto.setFirstName(user.get().getFirstName());
		userDto.setLastName(user.get().getLastName());
		userDto.setPassword(user.get().getPassword());
		userDto.setState(user.get().getState());

		userDto.setRoles(RoleMapper.toSetOfRoleDto(user.get().getRoles()));

		return userDto;
	}

	public static UserDto toUserDto(UserCreationDto userCreationDto) {

		UserDto userDto = new UserDto();

		userDto.setFirstName(userCreationDto.getFirstName());
		userDto.setLastName(userCreationDto.getLastName());
		userDto.setUsername(userCreationDto.getUsername());
		userDto.setPassword(userCreationDto.getPassword());

		return userDto;
	}

	public static UserDto toUserDto(UserCreationByAdminDto userCreationByAdminDto) {

		UserDto userDto = new UserDto();

		userDto.setFirstName(userCreationByAdminDto.getFirstName());
		userDto.setLastName(userCreationByAdminDto.getLastName());
		userDto.setUsername(userCreationByAdminDto.getUsername());
		userDto.setPassword(userCreationByAdminDto.getPassword());
		userDto.setState(userCreationByAdminDto.getState());
		userDto.setRoles(userCreationByAdminDto.getRoles());

		return userDto;
	}

	public static UserDto toUserDto(UserUpdateByAdminDto userUpdateByAdminDto) {

		UserDto userDto = new UserDto();

		userDto.setFirstName(userUpdateByAdminDto.getFirstName());
		userDto.setLastName(userUpdateByAdminDto.getLastName());
		userDto.setUsername(userUpdateByAdminDto.getUsername());
		userDto.setState(userUpdateByAdminDto.getState());
		userDto.setRoles(userUpdateByAdminDto.getRoles());

		return userDto;
	}

	public static List<UserDto> toListOfUserDto(Page<User> users) {

		List<UserDto> userDtos = new ArrayList<UserDto>();

		for (User user : users) {

			UserDto userDto = new UserDto();

			userDto.setId(user.getId());
			userDto.setUsername(user.getUsername());
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setPassword(user.getPassword());
			userDto.setState(user.getState());

			userDto.setRoles(RoleMapper.toSetOfRoleDto(user.getRoles()));

			userDtos.add(userDto);

		}
		return userDtos;

	}

	public static User toUser(UserDto userDto) {

		User user = new User();

		user.setId(userDto.getId());
		user.setUsername(userDto.getUsername());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setPassword(userDto.getPassword());
		user.setState(userDto.getState());

		user.setRoles(RoleMapper.toSetOfRole(userDto.getRoles()));

		return user;
	}

	public static UserPasswordDto toUserPasswordDto(UserDto userDto) {

		UserPasswordDto userPasswordDto = new UserPasswordDto();
		userPasswordDto.setPassword(userDto.getPassword());

		return userPasswordDto;
	}

	public static UserPasswordDto toUserPasswordDto(User user) {

		UserPasswordDto userPasswordDto = new UserPasswordDto();
		userPasswordDto.setPassword(user.getPassword());

		return userPasswordDto;
	}
}
