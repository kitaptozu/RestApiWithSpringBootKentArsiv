package com.kentarsivi.dto;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubFolderPathDto {

	@NotEmpty
	private List<@NotNull @NotBlank String> subFolderPath;

	@JsonIgnore
	public String getFullPathOfFolder() {

		String fullPathOfFolder = "";

		for (String path : subFolderPath) {

			fullPathOfFolder = fullPathOfFolder + File.separator + path;
		}
		return fullPathOfFolder;
	}

}
