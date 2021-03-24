package com.kentarsivi.dto.archive;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kentarsivi.dto.document.DocumentDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchiveDto {

	private Long id;

	@NotNull(message = "{constraints.NotEmpty.message}")
	@NotBlank
	@Size(min = 2, max = 60)
	private String name;

	private Set<DocumentDto> documents;

}
