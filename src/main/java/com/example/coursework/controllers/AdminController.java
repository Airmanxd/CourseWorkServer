package com.example.coursework.controllers;

import com.example.coursework.services.VideoService;
import com.example.coursework.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private UserService userService;

    /**
     * GET handler for admin access to the user deletion page
     * @return user delete page
     */
    @GetMapping("/deleteUser")
    public String deleteUser()
    {
        return "admin/deleteUser";
    }

    /**
     * POST handler for user deletion by an admin
     * @param username
     * @return shows if the user was deleted successfully
     */
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
