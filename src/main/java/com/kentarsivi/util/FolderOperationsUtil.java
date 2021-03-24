package com.kentarsivi.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FolderOperationsUtil {

	@Autowired
	private String archiveRootFolderPath;

	public Boolean createFolder(String folderPath) {

		File folder = new File(folderPath);

		if (!folder.exists()) {

			if (folder.mkdir()) {
				return true;
			} else {
				return false;
			}

		} else {
			return null;
		}

	}

	public Boolean isFolderExist(String folderPath) {

		File folder = new File(folderPath);

		if (folder.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public String[] getSubFolderNames(String fullPathOfFolder) {

		File folder = new File(fullPathOfFolder);

		String[] foldersArray = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		return foldersArray;
	}

	public long getSizeOfFolder(String folder) throws IOException {

		Path folderPath = Paths.get(folder);
		long size = Files.walk(folderPath).filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();

		return size;

	}
}
