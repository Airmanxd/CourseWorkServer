package com.example.coursework.controllers;

import com.example.coursework.models.Video;
import com.example.coursework.services.VideoService;
import com.example.coursework.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private UserService userService;

    @GetMapping("/deleteUser")
    public String deleteUser()
    {
        return "admin/deleteUser";
    }
    @PostMapping("/deleteUser")
    public @ResponseBody String deleteUser(@RequestParam("username") String username)
    {
        boolean success = userService.deleteUser(username);
        if(success)
            return "Deleted successfully!";
        else
            return "Error!";
    }

}
