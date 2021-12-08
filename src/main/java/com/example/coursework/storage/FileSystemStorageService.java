package com.example.coursework.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import com.example.coursework.configs.StorageProperties;
import com.example.coursework.storage.StorageException;
import com.example.coursework.storage.StorageFileNotFoundException;
import com.example.coursework.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service

@Slf4j
public class FileSystemStorageService implements StorageService {

	/**
	 * path to the files folder
	 */
	private final Path rootLocation;


	/**
	 * @param properties storage config
	 */
	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	/**
	 * Saves the file in a directory specified in the storage config
	 * @param file file to save
	 */
	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	/**
	 * loads all the videos in the storage folder
	 * @return loads all the videos in the storage folder
	 */
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	/**
	 * returns the path to the specified file
	 * @param filename name of the file
	 * @return returns the path to the specified file
	 */
	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	/**
	 * returns specified file as Resource
	 * @param filename name of the file
	 * @return returns specified file as Resource
	 */
	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	/**
	 * deletes all files
	 */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	/**
	 * deletes the specified file
	 * @param name name of the video
	 */
	@Override
	public void deleteByName(String name) {
		log.info("Trying to delete the file: " + name);

		File target = Paths.get(rootLocation.toString()+"\\"+name).toFile();

		log.info("File path: " + rootLocation.toString()+"\\"+name);

		if (target.delete()) {
			log.info("Deleted the file: " + target.getName());
		} else {
			log.info("Deleted the file: " + target.getName());
		}
	}

	/**
	 * initialization
	 */
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
