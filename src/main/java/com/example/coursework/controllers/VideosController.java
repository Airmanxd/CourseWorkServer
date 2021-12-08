package com.example.coursework.controllers;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.coursework.models.User;
import com.example.coursework.models.Video;
import com.example.coursework.storage.MyResourceHttpRequestHandler;
import com.example.coursework.storage.StorageService;
import com.example.coursework.services.UserService;
import com.example.coursework.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.coursework.storage.StorageFileNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/videos")
public class VideosController {
    private final VideoService videoService;
    private final StorageService storageService;
    private final UserService userService;
    private final MyResourceHttpRequestHandler handler;
    @Autowired
    public VideosController(MyResourceHttpRequestHandler handler, StorageService storageService, VideoService videoService, UserService userService) {
        this.storageService = storageService;
        this.videoService = videoService;
        this.userService = userService;
        this.handler = handler;
    }

    /**
     * GET handler for the first access to the videos page
     * @param model
     * @param principal
     * @return videos page with all the videos
     * @throws IOException
     */
    @GetMapping()
    public String initial(Model model, Principal principal) throws IOException
    {
        List<Video> videos = videoService.getAll();
        videos.sort(Comparator.comparing(Video::getName));
        model.addAttribute("videos", videos);
        Boolean categorized = false;
        model.addAttribute("categorized",categorized);
        return "videos";
    }

    /**
     * POST handler for categorization of videos on the videos page
     * @param model
     * @param category
     * @param principal
     * @return videos page with categorized videos only
     */
    @PostMapping("/categorized")
    public String categorized(Model model, @ModelAttribute("category") String category, Principal principal){

        if (category == "Liked"){
            User user = (User) userService.loadUserByUsername(principal.getName());
            model.addAttribute("videos", user.getLikedVideos());
            return "videos";
        }
        if(category.equals("All"))
            model.addAttribute("videos", videoService.getAll());
        else
            model.addAttribute("videos", videoService.getByCategory(category));
                Boolean categorized = true;
        model.addAttribute("categorized",categorized);
        return "videos";
    }

    /**
     * GET handler for upload request
     * @param model
     * @return upload page
     * @throws IOException
     */
    @GetMapping("/upload")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("video", new Video());
        return "upload";
    }

    /**
     * POST handler for video uploads
     * @param file MultipartFile from the request
     * @param redirectAttributes
     * @param video Video object from the client form
     * @param principal
     * @return redirects to user channel
     * @throws IOException
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Video video, Principal principal) throws IOException{
        User user = (User) userService.loadUserByUsername(principal.getName());
        videoService.addVideo(video, file, user);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/channel";
    }

    /**
     * GET handler for admin video deletetion requests
     * @param id videoId from request
     * @return redirects to the videos page
     */
    @GetMapping("/deleteVideo/{id}")
    public String deleteVideo(@PathVariable("id") Long id)
    {
        videoService.deleteVideo(id);
        return "redirect:/videos";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


    /**
     * GET handler for video load requests
     * @param id videoId from the request
     * @return FileSystemResource of the videofile requested
     */
    @GetMapping(path = "/{id}", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource plain( @PathVariable("id") Long id) {
        Video video = videoService.getById(id);
        return new FileSystemResource(video.getSourcePath());

    }

    /**
     * GET handler for like requests
     * @param id videoId from request
     * @param principal
     * @return redirects to the videos page
     */
    @GetMapping("/like/{id}")
    public String like(@PathVariable("id") Long id, Principal principal){
        User user = (User) userService.loadUserByUsername(principal.getName());
        Video video = videoService.getById(id);
        videoService.like(id, user);
        return "redirect:/videos";
    }
}
