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

    @PostMapping("/categorized")
    public String categorized(Model model, @ModelAttribute("category") String category, Principal principal){

        if (category == "Liked"){
            User user = (User) userService.loadUserByUsername(principal.getName());
            model.addAttribute("videos", user.getLikedVideos());
            return "videos";
        }

        List<Video> videos = videoService.getAll();
        videos.sort(Comparator.comparing(Video::getName));
        if(!category.equals("All")){
            List<Video> result = new ArrayList<>();
            for(Video video : videos){
                if(video.getCategory().equals(category))
                    result.add(video);
            }
            model.addAttribute("videos", result);
        }
        else
            model.addAttribute("videos", videos);
        Boolean categorized = true;
        model.addAttribute("categorized",categorized);
        return "videos";
    }
    @GetMapping("/upload")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("video", new Video());
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Video video, Principal principal) throws IOException{
        User user = (User) userService.loadUserByUsername(principal.getName());
        videoService.addVideo(video, file, user);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/channel";
    }

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

//    @GetMapping("/{path}")
//    public void getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("path") String path) throws ServletException, IOException {
//
//        request.setAttribute(MyResourceHttpRequestHandler.ATTR_FILE, path);
//        handler.handleRequest(request, response);
//    }

    @GetMapping(path = "/{id}", produces = "video/mp4")
    @ResponseBody
    public FileSystemResource plain( @PathVariable("id") Long id) {
        Video video = videoService.getById(id);
        return new FileSystemResource(video.getSourcePath());

    }
    @GetMapping("/like/{id}")
    public String like(@PathVariable("id") Long id, Principal principal){
        User user = (User) userService.loadUserByUsername(principal.getName());
        Video video = videoService.getById(id);
        videoService.like(id, user);
        return "redirect:/videos";
    }
}
