package com.kentarsivi.controller.v1.api;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kentarsivi.dto.FolderCreationDto;
import com.kentarsivi.dto.SubFolderPathDto;
import com.kentarsivi.dto.SubFoldersListDto;
import com.kentarsivi.dto.archive.ArchiveDto;
import com.kentarsivi.dto.document.DocumentCreationDto;
import com.kentarsivi.dto.document.DocumentDto;
import com.kentarsivi.dto.document.DocumentUpdateDto;
import com.kentarsivi.dto.mapper.ArchiveMapper;
import com.kentarsivi.dto.mapper.DocumentMapper;
import com.kentarsivi.dto.mapper.RoleMapper;
import com.kentarsivi.dto.mapper.UserMapper;
import com.kentarsivi.dto.role.RoleDto;
import com.kentarsivi.dto.user.UserCreationByAdminDto;
import com.kentarsivi.dto.user.UserDto;
import com.kentarsivi.dto.user.UserPasswordDto;
import com.kentarsivi.dto.user.UserUpdateByAdminDto;
import com.kentarsivi.dto.user.UserUpdateDto;
import com.kentarsivi.exception.ArchiveNotFoundException;
import com.kentarsivi.exception.DocumentNotFoundException;
import com.kentarsivi.exception.FolderNotFoundException;
import com.kentarsivi.exception.UserNotFoundException;
import com.kentarsivi.model.Archive;
import com.kentarsivi.model.Role;
import com.kentarsivi.service.CustomUserDetails;
import com.kentarsivi.service.KentArsiviService;
import com.kentarsivi.util.FileOperationsUtil;
import com.kentarsivi.util.FolderOperationsUtil;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private KentArsiviService kentArsiviService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private FileOperationsUtil fileOperationsUtil;

	@Autowired
	private FolderOperationsUtil folderOperationsUtil;

	@Autowired
	private String archiveRootFolderPath;

	// "/Users/mustafaalp/Desktop/KentArsiviRootFolder";

	// Admin user settings

	@RequestMapping(value = "/admins", method = RequestMethod.GET)
	public ResponseEntity<UserDto> getAdminSelf(Authentication authentication) {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		Long id = customUserDetails.getId();

		UserDto userDto = kentArsiviService.findUserById(id);

		userDto.setPassword(null);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);

	}

	@RequestMapping(value = "/admins", method = RequestMethod.PUT)
	public ResponseEntity<UserDto> updateAdminSelf(@RequestBody @Valid UserUpdateDto userUpdateDto,
			Authentication authentication) throws Exception {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		Long id = customUserDetails.getId();

		UserDto userDto = kentArsiviService.findUserById(id);

		if (!userDto.getUsername().trim().equals(userUpdateDto.getUsername().trim())
				&& kentArsiviService.isUserExistByUsername(userUpdateDto.getUsername().trim())) {

			throw new Exception("USERNAME ALREADY EXIST");
		}
		userDto.setId(id);
		userDto.setFirstName(userUpdateDto.getFirstName());
		userDto.setLastName(userUpdateDto.getLastName());
		userDto.setUsername(userUpdateDto.getUsername());

		// state admin tarafindan yonetilebilecek
		// roller admin tarafindan belirlenebilecek

		userDto = kentArsiviService.updateUser(userDto);

		userDto.setPassword(null);
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);

	}

	@RequestMapping(value = "/admins/password", method = RequestMethod.PUT) // body = {"password":"12345"}
	public ResponseEntity<UserPasswordDto> changeUserPassword(@RequestBody @Valid UserPasswordDto userPasswordDto,
			Authentication authentication) throws Exception {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		Long id = customUserDetails.getId();

		UserDto curretUserDto = kentArsiviService.findUserById(id);

		String encryptPwd = passwordEncoder.encode(userPasswordDto.getPassword().trim());

		curretUserDto.setPassword(encryptPwd);

		curretUserDto = kentArsiviService.updateUser(curretUserDto);

		return new ResponseEntity<UserPasswordDto>(userPasswordDto, HttpStatus.OK);

	}

	// End of Admin User settings

//---------------------------------------------------------------------------------------------------------------------------------------------//

	// General User Settings

	/*
	 * { "username": "admin2", "password": "12345", "firstName": "Mustafa Alp",
	 * "lastName": "Cetin", "roles": [ { "role": "ADMIN" } ] }
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users")
	public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreationByAdminDto userCreationByAdminDto)
			throws Exception {

		UserDto userDto = UserMapper.toUserDto(userCreationByAdminDto);

		if (kentArsiviService.isUserExistByUsername(userDto.getUsername()) || userDto.getUsername().equals("admin")) {

			throw new Exception("USERNAME ALREADY EXIST");
		}

		Set<Role> roles = new HashSet<>();

		for (Role role : RoleMapper.toSetOfRole(userDto.getRoles())) {

			RoleDto roleDto = kentArsiviService.findRoleByRoleName(role.getRole());

			if (roleDto == null) {
				throw new RoleNotFoundException("ROLE NOT FOUND");
			}
			role.setId(roleDto.getId());
			role.setRole(roleDto.getRole());
			role.setUsers(null);

			roles.add(role);

		}

		userDto.setRoles(RoleMapper.toSetOfRoleDto(roles));

		String pwd = userDto.getPassword();
		String encryptPwd = passwordEncoder.encode(pwd);
		userDto.setPassword(encryptPwd);

		userDto.setState(true);

		userDto = kentArsiviService.createUser(userDto);

		userDto.setPassword(null);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserUpdateByAdminDto userUpdateByAdminDto,
			@PathVariable("id") Long id, Authentication authentication) throws Exception {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		UserDto userDto = UserMapper.toUserDto(userUpdateByAdminDto);
		userDto.setId(id);

		if (id == customUserDetails.getId()) {
			throw new Exception("CANNOT UPDATE SELF THIS METHOD");
		} else if (!kentArsiviService.isUserExistById(id)) {
			throw new UserNotFoundException("USER NOT FOUND");
		}

		UserDto curretUserDto = kentArsiviService.findUserById(id);

		if (!curretUserDto.getUsername().trim().equals(userDto.getUsername().trim())
				&& kentArsiviService.isUserExistByUsername(userDto.getUsername().trim())) {

			throw new Exception("USERNAME ALREADY EXIST");
		}

		curretUserDto.setId(id);
		curretUserDto.setFirstName(userDto.getFirstName());
		curretUserDto.setLastName(userDto.getLastName());
		curretUserDto.setUsername(userDto.getUsername());
		// password ayri olarak islenecek
		curretUserDto.setState(userDto.getState());

		Set<Role> roles = new HashSet<>();

		for (Role role : RoleMapper.toSetOfRole(userDto.getRoles())) {

			RoleDto roleDto = kentArsiviService.findRoleByRoleName(role.getRole());

			if (roleDto == null) {
				throw new RoleNotFoundException("ROLE NOT FOUND");
			}
			role.setId(roleDto.getId());
			role.setRole(roleDto.getRole());
			role.setUsers(null);
			roles.add(role);

		}

		curretUserDto.setRoles(RoleMapper.toSetOfRoleDto(roles));

		curretUserDto = kentArsiviService.updateUser(curretUserDto);

		curretUserDto.setPassword(null);
		return new ResponseEntity<UserDto>(curretUserDto, HttpStatus.OK);

	}

	@RequestMapping(value = "/users/{id}/password", method = RequestMethod.PUT) // body = {"password":"12345"}
	public ResponseEntity<Void> changeUserPassword(@RequestBody @Valid UserPasswordDto userPasswordDto,
			@PathVariable Long id, Authentication authentication) throws Exception {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		if (id == customUserDetails.getId()) {
			throw new Exception("CANNOT UPDATE SELF PASSWORD WITH THIS METHOD");
		} else if (!kentArsiviService.isUserExistById(id)) {
			throw new UserNotFoundException("USER NOT FOUND");
		}

		UserDto curretUserDto = kentArsiviService.findUserById(id);

		String encryptPwd = passwordEncoder.encode(userPasswordDto.getPassword());
		curretUserDto.setPassword(encryptPwd);

		curretUserDto = kentArsiviService.updateUser(curretUserDto);

		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	// localhost:8080/rest/admin/users?page=0&pageSize=5
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam Integer pageSize, @RequestParam Integer page)
			throws Exception {

		Pageable pageable = PageRequest.of(page, pageSize);

		Page<UserDto> userDtos = kentArsiviService.findAllUsers(pageable);

		if (userDtos.getSize() <= 0)
			throw new UserNotFoundException("USER NOT FOUND");

		return new ResponseEntity<Page<UserDto>>(userDtos, HttpStatus.OK);

	}

	// localhost:8080/rest/admin/users?name=admin&page=0&pageSize=5
	@RequestMapping(value = "/users/name", method = RequestMethod.GET)
	public ResponseEntity<Page<UserDto>> getUserByName(@RequestParam Integer pageSize, @RequestParam Integer page,
			@RequestParam @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") String name) throws Exception {

		Pageable pageable = PageRequest.of(page, pageSize);

		Page<UserDto> userDtos = kentArsiviService.findUsersByName(name, pageable);

		if (userDtos.getSize() <= 0)
			throw new UserNotFoundException("USER NOT FOUND");

		return new ResponseEntity<Page<UserDto>>(userDtos, HttpStatus.OK);

	}

	// localhost:8080/rest/admin/users/id/12
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public ResponseEntity<UserDto> getUserById(
			@PathVariable("id") @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") @Size(min = 0) Long id)
			throws Exception {

		if (!kentArsiviService.isUserExistById(id)) {
			throw new UserNotFoundException("USER NOT FOUND");
		}

		UserDto userDto = kentArsiviService.findUserById(id);
		userDto.setPassword(null);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);

	}

	// localhost:8080/rest/admin/users/username?username=admin
	@RequestMapping(value = "/users/username", method = RequestMethod.GET)
	public ResponseEntity<UserDto> getUserByUsername(
			@RequestParam("username") @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") String username)
			throws Exception {

		UserDto userDto = kentArsiviService.findUserByUsername(username);

		if (userDto == null) {
			throw new UserNotFoundException("USER NOT FOUND");
		}
		userDto.setPassword(null);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);

	}

	// localhost:8080/rest/admin/users/12
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteUserById(
			@PathVariable("id") @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") @Size(min = 0) Long id,
			Authentication authentication) throws Exception {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		if (customUserDetails.getId() == id)
			throw new Exception("CANNOT DELETE SELF");
		else if (!kentArsiviService.isUserExistById(id))
			throw new UserNotFoundException("USER NOT FOUND");

		kentArsiviService.deleteUserById(id);

		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	// End of General User Settings

//---------------------------------------------------------------------------------------------------------------------------------------------//

	// Document Settings

	@RequestMapping(method = RequestMethod.POST, value = "/documents")
	public ResponseEntity<DocumentDto> createDocument(@RequestBody @Valid DocumentCreationDto documentCreationDto)
			throws Exception {

		DocumentDto documentDto = DocumentMapper.toDocumentDto(documentCreationDto);

		if (kentArsiviService.isDocumentExistByCode(documentDto.getCode().trim())) {

			throw new Exception("DOCUMENT ALREADY EXIST");

		} else if (!folderOperationsUtil
				.isFolderExist(archiveRootFolderPath + File.separator + documentDto.getFolderPath().trim())) {

			throw new FolderNotFoundException("FOLDER NOT FOUND");

		}

		ArchiveDto archiveDto = kentArsiviService.findArchiveByName(documentDto.getArchive().getName().trim());

		if (archiveDto == null) {
			throw new ArchiveNotFoundException("ARCHIVE NOT FOUND");
		}

		Archive archive = ArchiveMapper.toArchive(archiveDto);
		archive.setDocuments(null);

		documentDto.setArchive(ArchiveMapper.toArchiveDto(archive));

		documentDto = kentArsiviService.createDocument(documentDto);

		return new ResponseEntity<DocumentDto>(documentDto, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/documents/{id}")
	public ResponseEntity<DocumentDto> updateDocument(@RequestBody @Valid DocumentUpdateDto documentUpdateDto,
			@PathVariable("id") @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") @Size(min = 0) Long id)
			throws Exception {

		DocumentDto documentDto = DocumentMapper.toDocumentDto(documentUpdateDto);

		if (!kentArsiviService.isDocumentExistById(id)) {
			throw new DocumentNotFoundException("DOCUMENT NOT FOUND");
		} else if (kentArsiviService.isDocumentExistByCode(documentDto.getCode().trim())) {
			throw new Exception("DOCUMENT ALREADY EXIST");
		} else if (!folderOperationsUtil
				.isFolderExist(archiveRootFolderPath + File.separator + documentDto.getFolderPath().trim())) {
			throw new FolderNotFoundException("FOLDER NOT FOUND");
		}

		ArchiveDto archiveDto = kentArsiviService.findArchiveByName(documentDto.getArchive().getName().trim());
		if (archiveDto == null) {
			throw new ArchiveNotFoundException("ARCHIVE NOT FOUND");
		}

		Archive archive = ArchiveMapper.toArchive(archiveDto);
		archive.setDocuments(null);

		documentDto.setId(id);
		documentDto.setArchive(ArchiveMapper.toArchiveDto(archive));

		documentDto = kentArsiviService.updateDocument(documentDto);

		return new ResponseEntity<DocumentDto>(documentDto, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/code", method = RequestMethod.GET)
	public ResponseEntity<Page<DocumentDto>> getDocumentByCode(@RequestParam Integer pageSize,
			@RequestParam Integer page,
			@RequestParam @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") String code) throws Exception {

		Pageable pageable = PageRequest.of(page, pageSize);

		Page<DocumentDto> responseDocumentDtos = kentArsiviService.findDocumentsByCode(code, pageable);

		if (responseDocumentDtos.getSize() <= 0)
			throw new DocumentNotFoundException("DOCUMENT NOT FOUND");

		return new ResponseEntity<Page<DocumentDto>>(responseDocumentDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/description", method = RequestMethod.GET)
	public ResponseEntity<Page<DocumentDto>> getDocumentByDescription(@RequestParam Integer pageSize,
			@RequestParam Integer page,
			@RequestParam @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") String description)
			throws Exception {

		Pageable pageable = PageRequest.of(page, pageSize);

		Page<DocumentDto> responseDocumentDtos = kentArsiviService.findDocumentsByDescription(description, pageable);

		if (responseDocumentDtos.getSize() <= 0)
			throw new DocumentNotFoundException("DOCUMENT NOT FOUND");

		return new ResponseEntity<Page<DocumentDto>>(responseDocumentDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteDocumentsById(
			@PathVariable("id") @NotBlank @NotNull(message = "{constraints.NotEmpty.message}") @Size(min = 0) Long id)
			throws Exception {

		if (!kentArsiviService.isDocumentExistById(id))
			throw new DocumentNotFoundException("DOCUMENT NOT FOUND");

		kentArsiviService.deleteDocumentById(id);

		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	// End of Document Settings
//---------------------------------------------------------------------------------------------------------------------------------------------//

	
	// Bu ozellik adminin dosya yukleme ozelligini program uzerinden yapmamasi dusunulerek iptal edilecektir
	@RequestMapping(method = RequestMethod.POST, value = "/folders")
	public ResponseEntity<?> createSubFolder(@RequestBody @Valid FolderCreationDto folderCreationDto) throws Exception {

		Boolean creationFolderResult = folderOperationsUtil
				.createFolder(archiveRootFolderPath + File.separator + folderCreationDto.getFolderPath());

		if (creationFolderResult == true) {
			return new ResponseEntity<Void>(HttpStatus.OK);

		} else if (creationFolderResult == null) {
			throw new Exception("FOLDER ALREADY EXIST!");

		} else {

			throw new Exception("FOLDER CREATION FAILED");

		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/folders/sub")
	
	public ResponseEntity<SubFoldersListDto> getFolders(@RequestBody @Valid SubFolderPathDto subFolderPathDto)
			throws Exception {

		String folderPath = subFolderPathDto.getFullPathOfFolder();

		if (!folderOperationsUtil.isFolderExist(archiveRootFolderPath + File.separator + folderPath)) {

			throw new FolderNotFoundException("FOLDER NOT FOUND");
		}

		String fullPathOfFolders = archiveRootFolderPath + File.separator + folderPath;

		SubFoldersListDto response = new SubFoldersListDto();
		response.setSubFolders(Arrays.asList(folderOperationsUtil.getSubFolderNames(fullPathOfFolders)));

		return new ResponseEntity<SubFoldersListDto>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/folders")
	public ResponseEntity<Long> updateDocumentsFolderPaths(@RequestBody @Valid SubFolderPathDto subFolderPathDto)
			throws Exception {

		String folderPath = subFolderPathDto.getFullPathOfFolder();

		if (!folderOperationsUtil.isFolderExist(archiveRootFolderPath + File.separator + folderPath)) {

			throw new FolderNotFoundException("FOLDER NOT FOUND");
		}

		String fullPathOfFolders = archiveRootFolderPath + File.separator + folderPath;

		List<String> subFolders = Arrays.asList(folderOperationsUtil.getSubFolderNames(fullPathOfFolders));
		Long countOfUpdatedDocuments = 0L;

		for (String subFolder : subFolders) {

			countOfUpdatedDocuments += kentArsiviService.updateDocumentFolderPath(subFolder,
					((folderPath + File.separator + subFolder).replaceAll("//", "/")).replace("//", "/"));

		}

		return new ResponseEntity<Long>(countOfUpdatedDocuments, HttpStatus.OK);
	}

	// Bu ozellik adminin dosya yukleme ozelligini program uzerinden yapmamasi dusunulerek iptal edilecektir
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public ResponseEntity<Void> uploadFiles(@RequestParam("files") MultipartFile[] files,
			@RequestParam("folderPath") String folderName) {
		try {
			String fullPathForSave = archiveRootFolderPath + File.separator + folderName;
			if (folderOperationsUtil.createFolder(fullPathForSave) != false) {

				System.out.println("File list:");

				for (MultipartFile file : files) {
					fileOperationsUtil.saveFile(file, fullPathForSave);

					System.out.println("File name: " + file.getOriginalFilename());
					System.out.println("File Size: " + file.getSize());
					System.out.println("File type: " + file.getContentType());
					System.out.println("--------------------------------------");

				}
				return new ResponseEntity<Void>(HttpStatus.OK);
			} else {
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
	}

	// localhost:8080/rest/admin/archives
	@RequestMapping(value = "/archives", method = RequestMethod.GET)
	public ResponseEntity<Page<ArchiveDto>> getAllArchives() throws Exception {

		Pageable pageable = PageRequest.of(0, 20);

		Page<ArchiveDto> archiveDto = kentArsiviService.findAllArchives(pageable);

		return new ResponseEntity<Page<ArchiveDto>>(archiveDto, HttpStatus.OK);
	}

}


