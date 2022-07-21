package com.yjk.survey.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserJsonController {
    @GetMapping("/current-user.json")
    public Map<String, User> currentUser(HttpServletRequest req) {
        User currentUser = null;
        HttpSession session = req.getSession(false);
        log.debug("session: {}", session);
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            log.debug("currentUser: {}", currentUser);
        }

        HashMap<String, User> result = new HashMap<>();
        result.put("currentUser", currentUser);

        return result;
    }
}
