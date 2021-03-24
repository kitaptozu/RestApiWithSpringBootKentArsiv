package com.kentarsivi;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.kentarsivi.dto.archive.ArchiveDto;
import com.kentarsivi.dto.mapper.RoleMapper;
import com.kentarsivi.dto.role.RoleDto;
import com.kentarsivi.dto.user.UserDto;
import com.kentarsivi.model.Role;
import com.kentarsivi.service.KentArsiviService;

@Component
public class InitialFirstData {

	@Autowired
	private KentArsiviService kentArsiviService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	

	@EventListener
	public void appReady(ApplicationReadyEvent event) {

		if (!kentArsiviService.isRoleExistByRoleName("ADMIN")) {

			RoleDto roleDto = new RoleDto();
			roleDto.setRole("ADMIN");

			roleDto = kentArsiviService.createRole(roleDto);

			if (!kentArsiviService.isUserExistByRoleName("ADMIN")) {
				UserDto userDto = new UserDto();

				userDto.setFirstName("Administrator");
				userDto.setLastName("Administrator");
				userDto.setUsername("admin");
				userDto.setPassword("12345678");
				userDto.setState(true);

				String pwd = userDto.getPassword();
				String encryptPwd = passwordEncoder.encode(pwd);
				userDto.setPassword(encryptPwd);

				Set<Role> roles = new HashSet<>();
				Role role = new Role();
				role.setId(roleDto.getId());
				role.setRole(roleDto.getRole());
				roles.add(role);

				userDto.setRoles(RoleMapper.toSetOfRoleDto(roles));

				kentArsiviService.createUser(userDto);
			}

		}

		if (!kentArsiviService.isRoleExistByRoleName("USER")) {

			RoleDto roleDto = new RoleDto();
			roleDto.setRole("USER");

			roleDto = kentArsiviService.createRole(roleDto);

		}


		if (!kentArsiviService.isArchiveExist("OSMANLI")) {

			ArchiveDto archiveDto = new ArchiveDto();
			archiveDto.setId(null);
			archiveDto.setName("OSMANLI");
			archiveDto.setDocuments(null);

			kentArsiviService.createArchive(archiveDto);
		}

		if (!kentArsiviService.isArchiveExist("CUMHURİYET")) {

			ArchiveDto archiveDto = new ArchiveDto();
			archiveDto.setId(null);
			archiveDto.setName("CUMHURİYET");
			archiveDto.setDocuments(null);

			kentArsiviService.createArchive(archiveDto);
		}

	}

}
