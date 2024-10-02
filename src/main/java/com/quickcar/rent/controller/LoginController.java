package com.quickcar.rent.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	@Value("${frontend.base.url}")
	private String baseUrl;

    @GetMapping("/login")
    public String login() {
        return "redirect:"+baseUrl+"/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:\"+baseUrl+\"/login?loggedOut=true";
    }
    
    @GetMapping("/home")
    public String home() {
        return "redirect:\"+baseUrl+\"/home";
    }   
}
