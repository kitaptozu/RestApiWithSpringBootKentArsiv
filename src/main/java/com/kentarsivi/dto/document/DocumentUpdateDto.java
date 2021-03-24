package com.kentarsivi.dto.document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kentarsivi.dto.archive.ArchiveDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentUpdateDto {

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 2, max = 60)
	private String code;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 2, max = 3000)
	private String description;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(max = 254)
	private String folderPath;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	private ArchiveDto archive;
}
