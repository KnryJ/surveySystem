package com.yjk.survey.survey;

import com.yjk.survey.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/survey")
public class SurveyDoController {
    private final SurveyService surveyService;

    @Autowired
    public SurveyDoController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/create.do")
    public String create(String title, String brief, HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                surveyService.save(user.uid, title, brief);
                return "redirect:/survey/list.html";
            }
        }

        return "redirect:/user/login.html";
    }

    @PostMapping("/bind.do")
    public String bind(HttpServletRequest req, int suid, @RequestParam("bind-quid") int[] bindQuid) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                surveyService.bind(suid, bindQuid);
                return "redirect:/survey/list.html";
            }
        }

        return "redirect:/user/login.html";
    }
}
