package com.kentarsivi.dto.document;

import com.kentarsivi.dto.archive.ArchiveDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDto {

	private Long id;

	private String code;

	private String description;

	private String folderPath;

	private ArchiveDto archive;

}
