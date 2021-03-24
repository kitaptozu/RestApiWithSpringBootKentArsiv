package com.kentarsivi.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileOperationsUtil {

	/*
	 * 
	 * Farklı path'e sahip dizinleri tek bir zip dosyasına getirrmeyi sağlayan metod
	 * 
	 */
	public void makeZip(Set<String> folderPaths, OutputStream out) throws IOException {

		ZipOutputStream zipOut = new ZipOutputStream(out);

		for (String folderPath : folderPaths) {

			Path path = Paths.get(folderPath);

			if (Files.exists(path)) {
				String folderName = path.getFileName().toString();

				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

						zipOut.putNextEntry(
								new ZipEntry(folderName + File.separator + path.relativize(file).toString()));

						Files.copy(file, zipOut);
						zipOut.closeEntry();

						return FileVisitResult.CONTINUE;

					}
				});
			}

		}

		zipOut.finish();
		zipOut.close();
	}

	public Boolean isFileExist(String filePath) {

		File file = new File(filePath);

		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public String saveFile(MultipartFile file, String folderName) throws IOException {

		byte[] bytes = file.getBytes();

		String filename = file.getOriginalFilename();
		Path path = Paths.get(folderName + File.separator + filename);

		Files.write(path, bytes);
		return filename;

	}

}
