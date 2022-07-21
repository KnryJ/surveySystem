package com.yjk.survey.survey;

import com.yjk.survey.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/survey")
public class SurveyJsonController {
    private final SurveyService surveyService;

    @Autowired
    public SurveyJsonController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/list.json")
    public HashMap<String, Object> list(HttpServletRequest req) {
        User currentUser = null;
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                List<Object> list = surveyService.list(currentUser.uid);
                result.put("data", list);
            }
        }

        result.put("currentUser", currentUser);
        return result;
    }

    @GetMapping("/bind.json")
    public HashMap<String, Object> bind(HttpServletRequest req, int suid) {
        User currentUser = null;
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                Map<String, Object> map = surveyService.map(currentUser.uid, suid);
                result.put("data", map);
            }
        }

        result.put("currentUser", currentUser);
        return result;
    }
}
