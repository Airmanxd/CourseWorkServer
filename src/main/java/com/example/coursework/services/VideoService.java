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

    public boolean addVideo(Video video, MultipartFile file, User user) throws IOException {
        log.info(String.format("Trying to add a video %s", video.getName()));
        if(videoRepository.findByName(video.getName()) != null)
            return false;
        storageService.store(file);
        String path = Paths.get(storageProperties.getLocation()).resolve(
                Paths.get(file.getOriginalFilename()))
                .normalize().toAbsolutePath().toString();
        log.info(String.format("Video sourcepath: %s", "src/main/java/webapp/templates/videos/"+file.getOriginalFilename()));

        video.setOwner(user);
        user.add_to_channel(video);
        video.setSourcePath("src/main/webapp/templates/videos/"+file.getOriginalFilename());
        videoRepository.save(video);
        return true;
    }
    public List<Video> getAll()
    {
        return videoRepository.findAll();
    }

    public List<Video> getByCategory(String category)
    {
        return videoRepository.findByCategory(category);
    }

    public Video getByName(String name){ return videoRepository.findByName(name);}

    public Video getById(Long id){ return videoRepository.findByVideoId(id);}

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
