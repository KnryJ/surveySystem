package com.yjk.survey.activity;

import com.yjk.survey.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity")
public class ActivityJsonController {
    private final ActivityService activityService;

    @Autowired
    public ActivityJsonController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/list.json")
    public HashMap<String, Object> list(HttpServletRequest req) {
        User currentUser = null;
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                List<Object> list = activityService.list(currentUser.uid);
                result.put("data", list);
            }
        }

        result.put("currentUser", currentUser);
        return result;
    }

    @GetMapping("/exam.json")
    public HashMap<String, Object> exam(int acid) {
        HashMap<String, Object> result = new HashMap<>();
        // 根据 acid 获取 activity
        HashMap<String, Object> activity = activityService.get(acid);
        // 判断是否在合法时间之内
        Instant now = Instant.now();
        System.out.println(activity);
        if (now.compareTo((Instant)activity.get("startedAt")) < 0) {
            // 调查还未开始
            result.put("state", "未开始");
            return result;
        }

        if (now.compareTo((Instant)activity.get("endedAt")) > 0) {
            // 调查已经结束
            result.put("state", "已结束");
            return result;
        }
        // 根据 suid 获取调查信息
        HashMap<String, Object> survey = activityService.getSurvey((int)activity.get("suid"));
        // 根据 suid 和关系，获取题目信息
        List<Object> questionList = activityService.getQuestionList((int)activity.get("suid"));

        HashMap<String, Object> data = new HashMap<>();
        data.put("acid", acid);
        data.put("suid", activity.get("suid"));
        data.put("title", survey.get("title"));
        data.put("brief", survey.get("brief"));
        data.put("questionList", questionList);

        result.put("state", "进行中");
        result.put("data", data);

        return result;
    }

    @GetMapping("/detail.json")
    public Map<String, Object> detail(HttpServletRequest req, int acid) {
        User currentUser = null;
        HashMap<String, Object> result = new HashMap<>();
        HttpSession session = req.getSession(false);
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                HashMap<String, Object> activity = activityService.get(acid);
                int suid = (Integer) activity.get("suid");
                HashMap<String, Object> survey = activityService.getSurvey(suid);

                HashMap<String, Object> data = new HashMap<>();
                data.put("acid", acid);
                data.put("suid", suid);
                data.put("title", survey.get("title"));
                data.put("brief", survey.get("brief"));
                List<Result> resultList = activityService.getResultList(acid, suid);
                data.put("questionList", resultList);
                result.put("data", data);
            }
        }

        result.put("currentUser", currentUser);

        return result;
    }
}
