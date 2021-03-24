package com.kentarsivi.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kentarsivi.dto.document.DocumentCreationDto;
import com.kentarsivi.dto.document.DocumentDto;
import com.kentarsivi.dto.document.DocumentUpdateDto;
import com.kentarsivi.model.Document;

public class DocumentMapper {

	public static DocumentDto toDocumentDto(Document document) {

		DocumentDto documentDto = new DocumentDto();

		documentDto.setId(document.getId());
		documentDto.setCode(document.getCode());
		documentDto.setDescription(document.getDescription());
		documentDto.setFolderPath(document.getFolderPath());
		documentDto.setArchive(ArchiveMapper.toArchiveDto(document.getArchive()));

		return documentDto;
	}

	public static DocumentDto toDocumentDto(Optional<Document> document) {

		DocumentDto documentDto = new DocumentDto();

		documentDto.setId(document.get().getId());
		documentDto.setCode(document.get().getCode());
		documentDto.setDescription(document.get().getDescription());
		documentDto.setFolderPath(document.get().getFolderPath());
		documentDto.setArchive(ArchiveMapper.toArchiveDto(document.get().getArchive()));

		return documentDto;

	}

	public static DocumentDto toDocumentDto(DocumentCreationDto documentCreationDto) {

		DocumentDto documentDto = new DocumentDto();

		documentDto.setCode(documentCreationDto.getCode());
		documentDto.setDescription(documentCreationDto.getDescription());
		documentDto.setFolderPath(documentCreationDto.getFolderPath());
		documentDto.setArchive(documentCreationDto.getArchiveDto());

		return documentDto;
	}

	public static DocumentDto toDocumentDto(DocumentUpdateDto documentUpdateDto) {

		DocumentDto documentDto = new DocumentDto();

		documentDto.setCode(documentUpdateDto.getCode());
		documentDto.setDescription(documentUpdateDto.getDescription());
		documentDto.setFolderPath(documentUpdateDto.getFolderPath());
		documentDto.setArchive(documentUpdateDto.getArchive());

		return documentDto;
	}

	public static List<DocumentDto> toListOfDocumentDto(Page<Document> documents) {

		List<DocumentDto> documentDtos = new ArrayList<>();

		for (Document document : documents) {

			DocumentDto documentDto = DocumentMapper.toDocumentDto(document);

			documentDtos.add(documentDto);

		}

		return documentDtos;

	}

	public static Document toDocument(DocumentDto documentDto) {

		Document document = new Document();

		document.setId(documentDto.getId());
		document.setCode(documentDto.getCode());
		document.setDescription(documentDto.getDescription());
		document.setFolderPath(documentDto.getFolderPath());
		document.setArchive(ArchiveMapper.toArchive(documentDto.getArchive()));

		return document;
	}

}
