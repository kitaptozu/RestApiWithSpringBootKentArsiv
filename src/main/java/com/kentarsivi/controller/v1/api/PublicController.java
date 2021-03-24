package com.kentarsivi.controller.v1.api;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import com.kentarsivi.dto.AuthenticationRequestDto;
import com.kentarsivi.dto.AuthenticationResponseDto;
import com.kentarsivi.dto.mapper.RoleMapper;
import com.kentarsivi.dto.mapper.UserMapper;
import com.kentarsivi.dto.user.UserCreationDto;
import com.kentarsivi.dto.user.UserDto;
import com.kentarsivi.exception.UserNotFoundException;
import com.kentarsivi.model.Role;
import com.kentarsivi.security.JwtUtil;
import com.kentarsivi.service.CustomUserDetails;
import com.kentarsivi.service.CustomUserDetailsService;
import com.kentarsivi.service.KentArsiviService;

@RestController
@CrossOrigin
@RequestMapping("/api/public")
public class PublicController {

	@Autowired
	private KentArsiviService kentArsiviService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@RequestMapping(method = RequestMethod.POST, value = "/users")
	public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreationDto userCreationDto) throws Exception {

		UserDto userDto = UserMapper.toUserDto(userCreationDto);

		if (kentArsiviService.isUserExistByUsername(userDto.getUsername()) || userDto.getUsername().equals("admin")) {

			throw new Exception("USERNAME ALREADY EXIST");
		}

		Set<Role> roles = new HashSet<>();
		Role role = new Role();
		role.setId(null);
		role.setRole("USER");
		roles.add(role);

		userDto.setRoles(RoleMapper.toSetOfRoleDto(roles));

		userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

		userDto.setState(true);

		UserDto responseUserDto = kentArsiviService.createUser(userDto);
		responseUserDto.setPassword(null);

		return new ResponseEntity<UserDto>(responseUserDto, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationRequest(@RequestBody AuthenticationRequestDto authenticationRequestDto)
			throws Exception {

		if (!kentArsiviService.isUserExistByUsername(authenticationRequestDto.getUsername())) {

			throw new UserNotFoundException("USER NOT FOUND");
		}
		
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword()));

		final UserDetails userDetails = customUserDetailsService
				.loadUserByUsername(authenticationRequestDto.getUsername());

		final String jwt = jwtUtil.generateToken((CustomUserDetails)userDetails);

		return ResponseEntity.ok(new AuthenticationResponseDto(jwt));
	}

}
