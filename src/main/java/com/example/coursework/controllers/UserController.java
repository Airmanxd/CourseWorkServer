package com.example.coursework.controllers;

import com.example.coursework.models.Video;
import com.example.coursework.models.User;
import com.example.coursework.services.VideoService;
import com.example.coursework.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private VideoService videoService;

    /**
     * GET handler for user video deletion
     * @param id
     * @param principal
     * @return
     */
    @GetMapping("/deleteVideo/{id}")
    public String deleteVideo(@PathVariable("id") Long id, Principal principal)
    {
        videoService.deleteVideo(id);
        return "redirect:/channel";
    }


}
