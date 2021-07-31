package com.kentarsivi.controller.v1.api;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kentarsivi.dto.document.DocumentDto;
import com.kentarsivi.dto.user.UserDto;
import com.kentarsivi.dto.user.UserPasswordDto;
import com.kentarsivi.dto.user.UserUpdateDto;
import com.kentarsivi.exception.DocumentNotFoundException;
import com.kentarsivi.exception.FileNotFoundException;
import com.kentarsivi.exception.UserBadCredentialException;
import com.kentarsivi.service.CustomUserDetails;
import com.kentarsivi.service.KentArsiviService;
import com.kentarsivi.util.FileOperationsUtil;
import com.kentarsivi.util.FolderOperationsUtil;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

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

	@RequestMapping(value = "/documents/code", method = RequestMethod.GET)
	public ResponseEntity<Page<DocumentDto>> getDocumentByCode(@RequestParam Integer pageSize,
			@RequestParam Integer page, @RequestParam String code) throws Exception {

		Pageable pageable = PageRequest.of(page, pageSize);

		Page<DocumentDto> responseDocumentDtos = kentArsiviService.findDocumentsByCode(code, pageable);

		if (responseDocumentDtos.getSize() <= 0)
			throw new DocumentNotFoundException("DOCUMENT NOT FOUND");
		System.out.println("Hello Babus");
		return new ResponseEntity<Page<DocumentDto>>(responseDocumentDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/documents/description", method = RequestMethod.GET)
	public ResponseEntity<Page<DocumentDto>> getDocumentByDescription(@RequestParam Integer pageSize,
			@RequestParam Integer page, @RequestParam String description) throws Exception {

		Pageable pageable = PageRequest.of(page, pageSize);

		Page<DocumentDto> responseDocumentDtos = kentArsiviService.findDocumentsByDescription(description, pageable);

		if (responseDocumentDtos.getSize() <= 0)
			throw new DocumentNotFoundException("DOCUMENT NOT FOUND");

		return new ResponseEntity<Page<DocumentDto>>(responseDocumentDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<UserDto> getUser(Authentication authentication) {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		Long id = customUserDetails.getId();

		UserDto userDto = kentArsiviService.findUserById(id);

		userDto.setPassword(null);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);

	}

	@RequestMapping(value = "/users", method = RequestMethod.PUT)
	public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto,
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
		// password ayri olarak islenecek
		// state admin tarafindan yonetilebilecek
		// roller admin tarafindan belirlenebilecek

		userDto = kentArsiviService.updateUser(userDto);

		userDto.setPassword(null);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);

	}

	@RequestMapping(value = "/users/updatePassword", method = RequestMethod.PUT) // body = {"password":"12345"}
	public ResponseEntity<Void> changeUserPassword(@RequestBody @Valid UserPasswordDto userPasswordDto,
			Authentication authentication) throws Exception {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

		Long id = customUserDetails.getId();

		UserDto userDto = kentArsiviService.findUserById(id);
		System.out.println(userDto.getPassword());

		String encryptedPassword = passwordEncoder.encode(userPasswordDto.getPassword().trim());

		if (!userPasswordDto.getPassword().equals(userPasswordDto.getConfirmPassword())) {
			throw new UserBadCredentialException("PASSWORD NOT CONFIRMED");
		}

		if (!passwordEncoder.matches(userPasswordDto.getCurrentPassword(), userDto.getPassword())) {
			throw new UserBadCredentialException("PASSWORD DOES NOT MATCH");
		}
		userDto.setPassword(encryptedPassword);

		kentArsiviService.updateUser(userDto);

		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	// File islemleri

	@RequestMapping(value = "/documents/{id}/images", method = RequestMethod.GET)
	public ResponseEntity<List<String>> getDocumentImagesList(@PathVariable Long id) throws Exception {

		if (!kentArsiviService.isDocumentExistById(id))
			throw new Exception("DOCUMENT NOT FOUND.");

		String documentFolderPath = kentArsiviService.findDocumentFolderPathById(id);

		if (documentFolderPath == null)
			throw new Exception("DOCUMENT'S FOLDER PATH IS NULL");
		if (documentFolderPath.trim().equals(""))
			throw new Exception("DOCUMENT'S FOLDER PATH IS EMPTY");

		String fullDocumentFolderPath = archiveRootFolderPath + File.separator + documentFolderPath;

		System.out.println(fullDocumentFolderPath);

		if (!Files.exists(Paths.get(fullDocumentFolderPath)))
			throw new Exception("DOCUMENT'S FOLDER RECORD NOT UPADATED! FOLDER NOT FOUND");

		List<String> imageList = new ArrayList<String>();

		File documentFolder = new File(fullDocumentFolderPath);

		String[] acceptedFileExtensions = new String[] { ".jpg", ".png", ".jpeg", ".gif" };

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {

				for (String extension : acceptedFileExtensions) {

					if (name.toLowerCase().endsWith(extension)) {

						return true;

					}

				}
				return false;
			}
		};

		File[] files = documentFolder.listFiles(filter);

		for (File file : files) {

			// String lengthOfFile = String.format("%.4f", (Float.parseFloat(file.length() +
			// "") / 1048576));
			imageList.add(file.getName());

		}

		return new ResponseEntity<List<String>>(imageList, HttpStatus.OK);
	}

	@GetMapping(value = "/documents/{id}/downloads")
	public void downloadSpecificImageFile(@RequestParam("fileName") String fileName,
			@PathVariable Long id, HttpServletResponse response) throws Exception {

		if (!kentArsiviService.isDocumentExistById(id))
			throw new Exception("DOCUMENT NOT FOUND.");

		String documentFolderPath = kentArsiviService.findDocumentFolderPathById(id);

		if (documentFolderPath == null)
			throw new Exception("DOCUMENT'S FOLDER PATH IS NULL");
		if (documentFolderPath.trim().equals(""))
			throw new Exception("DOCUMENT'S FOLDER PATH IS EMPTY");

		String fullDocumentFolderPath = archiveRootFolderPath + File.separator + documentFolderPath + File.separator
				+ fileName;

		if (!Files.exists(Paths.get(fullDocumentFolderPath))) {
			throw new FileNotFoundException("IMAGE NOT FOUND");
		}
		/* ------ */

		File file = new File(fullDocumentFolderPath);
		Path path = Paths.get(file.getAbsolutePath());
		

		Files.copy(path, response.getOutputStream());
		response.getOutputStream().flush();
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/octet-stream");
		response.setContentLengthLong(file.length());
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=\"" + file.getName() + "\"");

	}

	@RequestMapping(value = "/documents/downloads/{documentIds}", method = RequestMethod.GET)
	public void downloadDocumentsByIdAsZip(@PathVariable Set<Long> documentIds, HttpServletResponse response)
			throws Exception {

		Set<String> folderPaths = new HashSet<>();
		long totalSizeOfFolders = 0;

		for (Long id : documentIds) {

			if (!kentArsiviService.isDocumentExistById(id))
				throw new Exception("DOCUMENT NOT FOUND.");

			String documentFolderPath = kentArsiviService.findDocumentFolderPathById(id);

			if (documentFolderPath == null)
				throw new Exception("DOCUMENT'S FOLDER PATH IS NULL");
			if (documentFolderPath.trim().equals(""))
				throw new Exception("DOCUMENT'S FOLDER PATH IS EMPTY");

			String fullDocumentFolderPath = archiveRootFolderPath + File.separator + documentFolderPath;

			Path path = Paths.get(fullDocumentFolderPath);
			if (Files.exists(path)) {
				totalSizeOfFolders += folderOperationsUtil.getSizeOfFolder(fullDocumentFolderPath);
				folderPaths.add(fullDocumentFolderPath);
			}

		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/octet-stream");
		response.addHeader("UnZippedContentLength", totalSizeOfFolders+"");
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "BelgeGoruntu.zip");

		fileOperationsUtil.makeZip(folderPaths, response.getOutputStream());
		
	}






}
