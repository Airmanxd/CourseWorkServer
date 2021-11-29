package com.example.coursework.controllers;

import com.example.coursework.models.User;
import com.example.coursework.models.Video;
import com.example.coursework.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping("index")
    public String home_page(){ return "redirect:/videos";}
    @GetMapping("")
    public String index(){ return "redirect:/videos";}

    @GetMapping("login")
    public String signIn() {
        return "login";
    }

    @PostMapping("login")
    public String login(){ return "redirect:/videos";}

    @GetMapping("registration")
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("registration")
    public String reg(User user) {
        userService.register(user);
        return "redirect:/videos";
    }

    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/videos";
    }

    @GetMapping("/channel")
    public String show(Model model, Principal principal)
    {
        User user = (User) userService.loadUserByUsername(principal.getName());

        List<Video> videos = user.getChannel();
        videos.sort(Comparator.comparing(Video::getName));
        Long total = 0L;
        model.addAttribute("videos", videos);
        for (Video video :
                videos) {
            total+=video.getLikes();
        }
        model.addAttribute("total", Long.toString(total));

        return "channel";
    }

    @GetMapping("error")
    public String error(){
        return "error";
    }
}
