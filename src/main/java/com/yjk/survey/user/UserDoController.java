package com.yjk.survey.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserDoController {
    private final UserService userService;

    @Autowired
    public UserDoController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register.do")
    public String register(String username, String password, HttpSession session) {
        User user = userService.register(username, password);
        log.debug("用户: {}", user);
        session.setAttribute("currentUser", user);
        return "redirect:/";
    }

    @PostMapping("/login.do")
    public String login(String username, String password, HttpSession session) {
        User user = userService.login(username, password);
        log.debug("用户: {}", user);
        if (user == null) {
            return "redirect:/user/login.html";
        }
        session.setAttribute("currentUser", user);
        return "redirect:/";
    }

    @GetMapping("/quit.do")
    public String quit(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute("currentUser");
        }

        return "redirect:/";
    }
}
