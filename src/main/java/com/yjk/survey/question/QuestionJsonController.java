package com.yjk.survey.question;

import com.yjk.survey.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionJsonController {
    private final QuestionService questionService;

    @Autowired
    public QuestionJsonController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/list.json")
    public HashMap<String, Object> list(HttpServletRequest req) {
        HashMap<String, Object> result = new HashMap<>();
        User currentUser = null;
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                List<Object> list = questionService.list(currentUser.uid);
                result.put("data", list);
            }
        }
        result.put("currentUser", currentUser);
        return result;
    }
}
