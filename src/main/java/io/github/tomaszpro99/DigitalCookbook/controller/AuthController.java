package io.github.tomaszpro99.DigitalCookbook.controller;

import io.github.tomaszpro99.DigitalCookbook.dto.UserDto;
import io.github.tomaszpro99.DigitalCookbook.entity.User;
import io.github.tomaszpro99.DigitalCookbook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    //metoda obsługująca żądanie przesłania formularza rejestracji użytkownika
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "Istnieje już konto zarejestrowane na ten adres email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token")String confirmationToken) {
        userService.confirmEmail(confirmationToken);
        return "redirect:/login?success";
    }
}