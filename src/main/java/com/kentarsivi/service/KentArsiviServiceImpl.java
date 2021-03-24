package com.kentarsivi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kentarsivi.dto.archive.ArchiveDto;
import com.kentarsivi.dto.document.DocumentDto;
import com.kentarsivi.dto.mapper.ArchiveMapper;
import com.kentarsivi.dto.mapper.DocumentMapper;
import com.kentarsivi.dto.mapper.RoleMapper;
import com.kentarsivi.dto.mapper.UserMapper;
import com.kentarsivi.dto.role.RoleDto;
import com.kentarsivi.dto.user.UserDto;
import com.kentarsivi.model.Archive;
import com.kentarsivi.model.Document;
import com.kentarsivi.model.Role;
import com.kentarsivi.model.User;
import com.kentarsivi.repository.ArchiveRepository;
import com.kentarsivi.repository.DocumentFullTextSearchRepository;
import com.kentarsivi.repository.DocumentRepository;
import com.kentarsivi.repository.RoleRepository;
import com.kentarsivi.repository.UserRepository;

@Service
public class KentArsiviServiceImpl implements KentArsiviService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private DocumentFullTextSearchRepository documentFullTextSearchRepository;

	@Autowired
	private ArchiveRepository archiveRepository;

	@Override
	@Transactional
	public UserDto createUser(UserDto userDto) {

		User user = UserMapper.toUser(userDto);
		user.setId(null);

		Set<Role> roles = new HashSet<>();

		user.getRoles().forEach(item -> {
			Role role = roleRepository.findByRole(item.getRole());
			if (role != null) {
				role.setUsers(null);
				roles.add(role);
			}
		});
		user.setRoles(roles);

		User createdUser = userRepository.save(user);
		userDto.setId(createdUser.getId());
		userDto.setRoles(RoleMapper.toSetOfRoleDto(roles));
		return userDto;
	}

	@Override
	public UserDto updateUser(UserDto userDto) {

		User user = UserMapper.toUser(userDto);

		userRepository.save(user);

		return userDto;
	}

	@Override
	public UserDto findUserById(Long id) {

		final Optional<User> user = userRepository.findById(id);

		UserDto userDto = UserMapper.toUserDto(user);

		return userDto;
	}

	@Override
	public UserDto findUserByUsername(String username) {

		final User user = userRepository.findByUsername(username);

		UserDto userDto = UserMapper.toUserDto(user);

		return userDto;
	}

	@Override
	public Page<UserDto> findUsersByName(String name, Pageable pageable) {

		Page<User> users = userRepository.findByFirstNameContainingOrLastNameContaining(name, name, pageable);

		List<UserDto> userDtos = UserMapper.toListOfUserDto(users);

		return new PageImpl<>(userDtos, pageable, users.getTotalElements());
	}

	@Override
	public Page<UserDto> findAllUsers(Pageable pageable) {
		Page<User> users = userRepository.findAll(pageable);

		List<UserDto> userDtos = UserMapper.toListOfUserDto(users);

		return new PageImpl<>(userDtos, pageable, users.getTotalElements());

	}

	@Override
	public Boolean isUserExistByUsername(String username) {
		Object usernameResponse = userRepository.checkUsername(username);
		if (usernameResponse != null) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Boolean isUserExistById(Long id) {
		Long userSizeById = userRepository.findUserCountById(id);
		if (userSizeById > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean isUserExistByRoleName(String role) {

		Long userSizeByRole = userRepository.findUserCountByRoleName(role);
		if (userSizeByRole > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void deleteUserById(Long id) {

		userRepository.deleteById(id);

	}

	@Override
	public RoleDto createRole(RoleDto roleDto) {

		Role role = RoleMapper.toRole(roleDto);
		role.setId(null);

		Role createdRole = roleRepository.save(role);

		roleDto.setId(createdRole.getId());

		return roleDto;
	}

	@Override
	public RoleDto updateRole(RoleDto roleDto) {

		Role role = RoleMapper.toRole(roleDto);

		roleRepository.save(role);

		return roleDto;
	}

	@Override
	public RoleDto findRoleById(Long id) {

		Optional<Role> role = roleRepository.findById(id);

		RoleDto roleDto = RoleMapper.toRoleDto(role);

		return roleDto;
	}

	@Override
	public RoleDto findRoleByRoleName(String roleName) {
		Role role = roleRepository.findByRole(roleName);
		if (role == null)
			return null;
		RoleDto roleDto = RoleMapper.toRoleDto(role);

		return roleDto;
	}

	@Override
	public List<RoleDto> findAllRoles(Pageable pageable) {

		Page<Role> roles = roleRepository.findAll(pageable);

		List<RoleDto> roleDtos = RoleMapper.toListOfRoleDto(roles);

		return roleDtos;
	}

	@Override
	public Boolean isRoleExistByRoleName(String roleName) {
		Boolean isExistRole = false;
		if (roleRepository.findRoleCountByRoleName(roleName) > 0) {
			isExistRole = true;
		}
		return isExistRole;
	}

	@Override
	public void deleteRoleById(Long id) {

		roleRepository.deleteById(id);

	}

	@Override
	public DocumentDto createDocument(DocumentDto documentDto) {

		Document document = DocumentMapper.toDocument(documentDto);
		document.setId(null);

		final Document createdDocument = documentRepository.save(document);

		documentDto.setId(createdDocument.getId());

		return documentDto;
	}

	@Override
	public DocumentDto updateDocument(DocumentDto documentDto) {

		Document document = DocumentMapper.toDocument(documentDto);

		documentRepository.save(document);

		return documentDto;
	}

	@Override
	public DocumentDto findDocumentById(Long id) {

		Optional<Document> document = documentRepository.findById(id);

		DocumentDto documentDto = DocumentMapper.toDocumentDto(document);

		return documentDto;
	}

	@Override
	public DocumentDto findDocumentByCode(String code) {

		Document document = documentRepository.findByCode(code);

		DocumentDto documentDto = DocumentMapper.toDocumentDto(document);

		return documentDto;
	}

	@Override
	public Page<DocumentDto> findDocumentsByCode(String code, Pageable pageable) {

		Page<Document> documents = documentRepository.findByCodeStartingWithIgnoreCaseAndFolderPathNotNull(code,
				pageable);

		List<DocumentDto> documentDtos = DocumentMapper.toListOfDocumentDto(documents);

		return new PageImpl<>(documentDtos, pageable, documents.getTotalElements());
	}

	@Override
	public Page<DocumentDto> findDocumentsByDescription(String description, Pageable pageable) {

		Page<Document> documents = documentFullTextSearchRepository.findDocumentByDescriptionFuzzySearch(pageable,
				description);
		List<DocumentDto> documentDtos = DocumentMapper.toListOfDocumentDto(documents);

		return new PageImpl<>(documentDtos, pageable, documents.getTotalElements());
	}

	@Override
	public Page<DocumentDto> findAllDocuments(Pageable pageable) {

		Page<Document> documents = documentRepository.findAll(pageable);

		List<DocumentDto> documentDtos = DocumentMapper.toListOfDocumentDto(documents);

		return new PageImpl<>(documentDtos, pageable, documents.getTotalElements());
	}

	@Override
	public String findDocumentFolderPathById(Long id) {

		return documentRepository.findDocumentPathById(id);
	}

	@Override
	public Boolean isDocumentExistById(Long id) {

		return documentRepository.existsById(id);
	}

	@Override
	public Boolean isDocumentExistByCode(String code) {

		if (documentRepository.findDocumentCountByCode(code) > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void deleteDocumentById(Long id) {

		documentRepository.deleteById(id);

	}

	@Override
	public int updateDocumentFolderPath(String code, String folderPath) {

		return documentRepository.updateDocumentFolderPath(code, folderPath);
	}

	@Override
	public ArchiveDto createArchive(ArchiveDto archiveDto) {

		Archive archive = ArchiveMapper.toArchive(archiveDto);
		archive.setId(null);

		Archive createdArchive = archiveRepository.save(archive);
		archiveDto.setId(createdArchive.getId());

		return archiveDto;
	}

	@Override
	public ArchiveDto updateArchive(ArchiveDto archiveDto) {

		Archive archive = ArchiveMapper.toArchive(archiveDto);

		archiveRepository.save(archive);

		return archiveDto;
	}

	@Override
	public ArchiveDto findArchiveById(Long id) {

		Optional<Archive> archive = archiveRepository.findById(id);

		ArchiveDto archiveDto = ArchiveMapper.toArchiveDto(archive);

		return archiveDto;
	}

	@Override
	public ArchiveDto findArchiveByName(String name) {

		Archive archive = archiveRepository.findByName(name);

		ArchiveDto archiveDto = ArchiveMapper.toArchiveDto(archive);

		return archiveDto;
	}

	@Override
	public Page<ArchiveDto> findAllArchives(Pageable pageable) {

		Page<Archive> archives = archiveRepository.findAll(pageable);

		List<ArchiveDto> archiveDtos = ArchiveMapper.toListOfArchiveDto(archives);

		return new PageImpl<>(archiveDtos, pageable, archives.getTotalElements());
	}

	@Override
	public void deleteArchiveById(Long id) {
		archiveRepository.deleteById(id);

	}

	@Override
	public Boolean isArchiveExist(String name) {
		Long archiveResponse = archiveRepository.findArchiveCountByName(name);
		if (archiveResponse > 0) {
			return true;
		} else {
			return false;
		}
	}

}
