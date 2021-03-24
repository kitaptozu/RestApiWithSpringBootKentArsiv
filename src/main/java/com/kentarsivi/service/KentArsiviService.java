package com.kentarsivi.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kentarsivi.dto.archive.ArchiveDto;
import com.kentarsivi.dto.document.DocumentDto;
import com.kentarsivi.dto.role.RoleDto;
import com.kentarsivi.dto.user.UserDto;

public interface KentArsiviService {

	// User

	// create
	public UserDto createUser(UserDto userDto);

	// update
	public UserDto updateUser(UserDto userDto);

	// select
	public UserDto findUserById(Long id);

	public UserDto findUserByUsername(String username);

	public Page<UserDto> findUsersByName(String name, Pageable pageable);

	public Page<UserDto> findAllUsers(Pageable pageable);

	public Boolean isUserExistByUsername(String username);

	public Boolean isUserExistById(Long id);

	public Boolean isUserExistByRoleName(String role);

	// delete
	public void deleteUserById(Long id);

	// Role

	// create
	public RoleDto createRole(RoleDto roleDto);

	// update
	public RoleDto updateRole(RoleDto roleDto);

	// select
	public RoleDto findRoleById(Long id);

	public RoleDto findRoleByRoleName(String roleName);

	public List<RoleDto> findAllRoles(Pageable pageable);

	public Boolean isRoleExistByRoleName(String roleName);

	// delete
	public void deleteRoleById(Long id);

	// Document

	// create
	public DocumentDto createDocument(DocumentDto documentDto);

	// update
	public DocumentDto updateDocument(DocumentDto documentDto);

	// select
	public DocumentDto findDocumentById(Long id);

	public DocumentDto findDocumentByCode(String code);

	public Page<DocumentDto> findDocumentsByCode(String code, Pageable pageable);

	public Page<DocumentDto> findDocumentsByDescription(String description, Pageable pageable);

	public Page<DocumentDto> findAllDocuments(Pageable pageable);

	public String findDocumentFolderPathById(Long id);

	public Boolean isDocumentExistById(Long id);

	public Boolean isDocumentExistByCode(String code);

	public int updateDocumentFolderPath(String code, String folderPath);

	// delete
	public void deleteDocumentById(Long id);

	// Archive

	// create
	public ArchiveDto createArchive(ArchiveDto archiveDto);

	// update
	public ArchiveDto updateArchive(ArchiveDto archiveDto);

	// select
	public ArchiveDto findArchiveById(Long id);

	public ArchiveDto findArchiveByName(String name);

	public Page<ArchiveDto> findAllArchives(Pageable pageable);

	public Boolean isArchiveExist(String name);

	// delete
	public void deleteArchiveById(Long id);

}
