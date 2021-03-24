package com.kentarsivi.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kentarsivi.dto.archive.ArchiveDto;
import com.kentarsivi.model.Archive;

public class ArchiveMapper {

	public static ArchiveDto toArchiveDto(Archive archive) {

		ArchiveDto archiveDto = new ArchiveDto();

		archiveDto.setId(archive.getId());
		archiveDto.setName(archive.getName());
		archiveDto.setDocuments(null);

		return archiveDto;

	}

	public static ArchiveDto toArchiveDto(Optional<Archive> archive) {

		ArchiveDto archiveDto = new ArchiveDto();

		archiveDto.setId(archive.get().getId());
		archiveDto.setName(archive.get().getName());
		archiveDto.setDocuments(null);

		return archiveDto;
	}

	public static List<ArchiveDto> toListOfArchiveDto(Page<Archive> archives) {

		List<ArchiveDto> archiveDtos = new ArrayList<>();

		for (Archive archive : archives) {
			ArchiveDto archiveDto = ArchiveMapper.toArchiveDto(archive);
			archiveDtos.add(archiveDto);
		}

		return archiveDtos;

	}

	public static Archive toArchive(ArchiveDto archiveDto) {

		Archive archive = new Archive();

		archive.setId(archiveDto.getId());
		archive.setName(archiveDto.getName());
		archive.setDocuments(null);

		return archive;

	}

}
