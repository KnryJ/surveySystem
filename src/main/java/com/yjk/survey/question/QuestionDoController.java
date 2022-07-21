package com.yjk.survey.question;

import com.yjk.survey.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/question")
public class QuestionDoController {
    private final QuestionService questionService;

    @Autowired
    public QuestionDoController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/create.do")
    public String create(String question, String optionA, String optionB, String optionC, String optionD, HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                questionService.save(user.uid, question, optionA, optionB, optionC, optionD);
                return "redirect:/question/create.html";
            }
        }

        return "redirect:/user/login.html";
    }
}
