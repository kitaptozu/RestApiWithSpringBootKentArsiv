package com.kentarsivi.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderCreationDto {

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 2)
	String folderPath;

}
