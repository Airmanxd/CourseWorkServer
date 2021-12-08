package com.example.coursework.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    /**
     * initialization
     */
    void init();
    /**
     * Saves the file in a directory specified in the storage config
     * @param file file to save
     */
    void store(MultipartFile file);
    /**
     * loads all the videos in the storage folder
     * @return loads all the videos in the storage folder
     */
    Stream<Path> loadAll();
    /**
     * returns the path to the specified file
     * @param filename name of the file
     * @return returns the path to the specified file
     */
    Path load(String filename);

    /**
     * returns specified file as Resource
     * @param filename name of the file
     * @return returns specified file as Resource
     */
    Resource loadAsResource(String filename);

    /**
     * deletes all files
     */
    void deleteAll();

    /**
     * deletes the specified file
     * @param name name of the video
     */
    void deleteByName(String name);
}
