package com.chris.main.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chris.main.models.User;
import com.chris.main.services.UserService;

@Controller
public class Users {
    private final UserService userService;
    
    public Users(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	if (result.hasErrors()) {
    		return "registrationPage.jsp";
    	} else {
    		userService.registerUser(user);
    		session.setAttribute("userId", user.getId());
    		return "redirect:/home";
    	}
        // if result has errors, return the registration page (don't worry about validations just now)
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, 
    						@RequestParam("password") String password, 
    						Model model, 
    						HttpSession session, 
    						RedirectAttributes flashMessage) {
        if (userService.authenticateUser(email, password) == true) {
        	session.setAttribute("userId", userService.findByEmail(email).getId());
        	return "redirect:/home";
        } else {
        	flashMessage.addFlashAttribute("error", "failed to login");
        	return "redirect:/login";
        }
    	// if the user is authenticated, save their user id in session
        // else, add error messages and return the login page
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
    	User user = userService.findUserById((Long) session.getAttribute("userId"));
    	model.addAttribute("user", user);
    	return "homePage.jsp";
        // get user from session, save them in the model and return the home page
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/login";
        // invalidate session
        // redirect to login page
    }
}
