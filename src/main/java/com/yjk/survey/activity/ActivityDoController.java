package com.yjk.survey.activity;

import com.yjk.survey.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/activity")
public class ActivityDoController {
    private final ActivityService activityService;

    @Autowired
    public ActivityDoController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/create.do")
    public String create(HttpServletRequest req, int suid, @RequestParam("started_at") String startedAt, @RequestParam("ended_at") String endedAt) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                activityService.save(user.uid, suid, startedAt, endedAt);
                return "redirect:/activity/list.html";
            }
        }

        return "redirect:/user/login.html";
    }

    @PostMapping("/exam.do")
    public String exam(HttpServletRequest req) {
        int acid = Integer.parseInt(req.getParameter("acid"));
        String nickname = req.getParameter("nickname");
        String phone = req.getParameter("phone");
        int gender = Integer.parseInt(req.getParameter("gender"));
        int age = Integer.parseInt(req.getParameter("age"));
        LinkedHashMap<Integer, String> answerMap = new LinkedHashMap<>();
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (!name.startsWith("quid-")) {
                continue;
            }

            int quid = Integer.parseInt(name.substring("quid-".length()));
            String answer = req.getParameter(name);
            System.out.println(quid + " -> " + answer);
            answerMap.put(quid, answer);
        }

        System.out.println(acid);
        System.out.println(nickname);
        System.out.println(phone);
        System.out.println(gender);
        System.out.println(age);
        System.out.println(answerMap);
        activityService.answer(acid, nickname, phone, gender, age, answerMap);

        return "redirect:/thanks.html";
    }
}
