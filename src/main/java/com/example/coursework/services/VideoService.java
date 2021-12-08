package com.example.coursework.services;

import com.example.coursework.configs.StorageProperties;
import com.example.coursework.models.User;
import com.example.coursework.models.Video;
import com.example.coursework.repositories.UserRepository;
import com.example.coursework.repositories.VideoRepository;
import com.example.coursework.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
@Transactional
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private StorageProperties storageProperties;

    /**
     * adds Video to DB and saves the associated videofile in the storage directory
     * @param video Video to add to DB
     * @param file Videofile to store
     * @param user User object of the client trying to add the video
     * @return true if succeeded in adding, false if video with this name already exists
     * @throws IOException
     */
    public boolean addVideo(Video video, MultipartFile file, User user) throws IOException {
        log.info(String.format("Trying to add a video %s", video.getName()));
        if(videoRepository.findByName(video.getName()) != null)
            return false;
        storageService.store(file);
        log.info(String.format("Video sourcepath: %s", "src/main/webapp/templates/videos/"+file.getOriginalFilename()));

        video.setOwner(user);
        user.add_to_channel(video);
        video.setSourcePath("src/main/webapp/templates/videos/"+file.getOriginalFilename());
        videoRepository.save(video);
        return true;
    }

    /**
     * @return all videos in the DB
     */
    public List<Video> getAll()
    {
        return videoRepository.findAll();
    }

    /**
     * @param category
     * @return List of Video objects of specified category
     */
    public List<Video> getByCategory(String category)
    {
        return videoRepository.findByCategory(category);
    }

    /**
     * @param name name of the video
     * @return Video object of specified name
     */
    public Video getByName(String name){ return videoRepository.findByName(name);}

    /**
     * @param id videoId
     * @return Video object of specified id
     */
    public Video getById(Long id){ return videoRepository.findByVideoId(id);}

    /**
     * deletes the specified Video from the db, associated with it videofile from the storage directory
     * and removes this video from the liked of users who had previously liked it
     * @param id videoId
     */
    public boolean deleteVideo(Long id) {
        Video video = videoRepository.findByVideoId(id);
        if(video == null)
            return false;
        User owner = video.getOwner();
        owner.remove_from_channel(video);
        List<User> users = video.getUsersLiked();
        for (User user : users){
            User edit = userRepository.findByUsername(user.getUsername());
            edit.removeFromLiked(video);
            userRepository.save(user);
        }
        storageService.deleteByName(video.getSourcePath().split("/")[5]);
        videoRepository.delete(video);
        return true;
    }

    /**
     * Adds a like to a video if the user hasn't liked the video yet, otherwise removes it
     * Also adds or removes the video from the users liked videos
     * @param id videoId to like or unlike
     * @param user User object of the client trying to like the video
     */
    public void like(Long id, User user){
        Video video = videoRepository.findByVideoId(id);
        List<Video> liked = user.getLikedVideos();
        if(liked.contains(video)){
            video.removeLike(user);
            user.removeFromLiked(video);
        }
        else{
            video.addLike(user);
            user.addToLiked(video);
        }
        videoRepository.save(video);
        userRepository.save(user);
    }
}
