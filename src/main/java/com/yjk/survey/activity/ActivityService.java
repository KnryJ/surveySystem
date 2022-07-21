package com.yjk.survey.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActivityService {
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;

    @Autowired
    public ActivityService(DataSource dataSource, ObjectMapper objectMapper) {
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public void save(int uid, int suid, String startedAt, String endedAt) {
        try (Connection c = dataSource.getConnection()) {
            String sql = "insert into activities (uid, suid, started_at, ended_at) values (?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, uid);
                ps.setInt(2, suid);
                ps.setString(3, startedAt);
                ps.setString(4, endedAt);

                ps.executeUpdate();
            }
        }
    }

    @SneakyThrows
    public List<Object> list(int uid) {
        Date now = new Date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try (Connection c = dataSource.getConnection()) {
            String sql = "SELECT acid, surveys.suid, title, started_at, ended_at FROM activities join surveys on activities.suid = surveys.suid where activities.uid = ? order by acid";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Object> list = new ArrayList<>();
                    while (rs.next()) {
                        HashMap<String, Object> m = new HashMap<>();
                        m.put("acid", rs.getInt("acid"));
                        m.put("suid", rs.getInt("suid"));
                        m.put("title", rs.getString("title"));
                        Timestamp startedAt = rs.getTimestamp("started_at");
                        Timestamp endedAt = rs.getTimestamp("ended_at");
                        String state;
                        log.info("现在时间: " + now.toInstant().toString());
                        log.info("开始时间: " + startedAt.toInstant().toString());
                        log.info("截至时间: " + endedAt.toInstant().toString());
                        if (startedAt.compareTo(now) > 0) {
                            state = "未开始";
                        } else if (endedAt.compareTo(now) >= 0) {
                            state = "进行中";
                        } else {
                            state = "已结束";
                        }
                        m.put("startedAt", startedAt.toLocalDateTime().format(formatter));
                        m.put("endedAt", endedAt.toLocalDateTime().format(formatter));
                        m.put("state", state);

                        list.add(m);
                    }
                    return list;
                }
            }
        }
    }

    @SneakyThrows
    public HashMap<String, Object> get(int acid) {
        try (Connection c = dataSource.getConnection()) {
            String sql = "select suid, started_at, ended_at from activities where acid = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, acid);

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    HashMap<String, Object> activity = new HashMap<>();
                    activity.put("acid", acid);
                    activity.put("suid", rs.getInt("suid"));
                    Timestamp startedAt = rs.getTimestamp("started_at");
                    System.out.println(startedAt.getTime());
                    activity.put("startedAt", Instant.ofEpochMilli(startedAt.getTime()));
                    Timestamp endedAt = rs.getTimestamp("ended_at");
                    System.out.println(endedAt.getTime());
                    activity.put("endedAt", Instant.ofEpochMilli(endedAt.getTime()));

                    return activity;
                }
            }
        }
    }

    @SneakyThrows
    public HashMap<String, Object> getSurvey(int suid) {
        String sql = "select title, brief from surveys where suid = ?";
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, suid);

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    HashMap<String, Object> survey = new HashMap<>();
                    survey.put("suid", suid);
                    survey.put("title", rs.getString("title"));
                    survey.put("brief", rs.getString("brief"));

                    return survey;
                }
            }
        }
    }

    @SneakyThrows
    public List<Object> getQuestionList(int suid) {
        try (Connection c = dataSource.getConnection()) {
            List<Integer> quidList = new ArrayList<>();
            String sql1 = "select quid from survey_question_relations where suid = ? order by sqrid";
            try (PreparedStatement ps = c.prepareStatement(sql1)) {
                ps.setInt(1, suid);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        quidList.add(rs.getInt("quid"));
                    }
                }
            }

            List<Object> list = new ArrayList<>();
            String sql2 = "select quid, question, options from questions where quid in (%s)";
            sql2 = String.format(sql2, String.join(", ", quidList.stream().map(String::valueOf).collect(Collectors.toList())));
            try (PreparedStatement ps = c.prepareStatement(sql2)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, Object> q = new HashMap<>();
                        q.put("quid", rs.getInt("quid"));
                        q.put("question", rs.getString("question"));
                        String optionsString = rs.getString("options");
                        List optionsList = objectMapper.readValue(optionsString, List.class);
                        q.put("options", optionsList);

                        list.add(q);
                    }
                }
            }

            return list;
        }
    }

    @SneakyThrows
    public void answer(int acid, String nickname, String phone, int gender, int age, LinkedHashMap<Integer, String> answerMap) {
        String answer = objectMapper.writeValueAsString(answerMap);

        try (Connection c = dataSource.getConnection()) {
            String sql = "insert into results (acid, nickname, phone, gender, age, answer) values (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, acid);
                ps.setString(2, nickname);
                ps.setString(3, phone);
                ps.setInt(4, gender);
                ps.setInt(5, age);
                ps.setString(6, answer);
                System.out.println(ps);
                ps.executeUpdate();
            }
        }
    }

    @SneakyThrows
    public List<Result> getResultList(int acid, int suid) {
        try (Connection c = dataSource.getConnection()) {
            // 根据 suid，查询出关联的 quid list
            List<Integer> quidList = new ArrayList<>();
            {
                String sql = "select quid from survey_question_relations where suid = ? order by sqrid";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, suid);

                    ClientPreparedStatement cps = (ClientPreparedStatement) ps;
                    PreparedQuery pq = (PreparedQuery) cps.getQuery();
                    System.out.println(pq.asSql());

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int quid = rs.getInt("quid");
                            quidList.add(quid);
                        }
                    }
                }
            }
            if (quidList.isEmpty()) {
                throw new RuntimeException("acid 为 1 的调查活动对应的调查问卷没有关联题目");
            }
            // 根据 quid list，查询出每道题的信息
            Map<Integer, Question> quidToQuestionMap = new HashMap<>();
            {
                String in = quidList.stream().map(String::valueOf).collect(Collectors.joining(", "));
                String sql = String.format("select quid, question, options from questions where quid in (%s) order by quid", in);
                try (PreparedStatement ps = c.prepareStatement(sql)) {

                    ClientPreparedStatement cps = (ClientPreparedStatement) ps;
                    PreparedQuery pq = (PreparedQuery) cps.getQuery();
                    System.out.println(pq.asSql());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int quid = rs.getInt("quid");
                            String question = rs.getString("question");
                            String optionJson = rs.getString("options");
                            List<String> options = objectMapper.readValue(optionJson, new TypeReference<List<String>>() {
                            });
                            Question item = new Question(quid, question, options);
                            quidToQuestionMap.put(quid, item);
                        }
                    }
                }
            }
            // 准备好一个结构，{ quid: { A: 0, B: 0, C: 0, D: 0 }, ... }；用于统计每道题中 A、B、C、D 的各自回答数量
            Map<Integer, AnswerCount> quidToAnswerCountsMap = new HashMap<>();
            for (Integer quid : quidList) {
                AnswerCount item = new AnswerCount(quid);
                quidToAnswerCountsMap.put(quid, item);
            }

            // 根据 acid 查出所有回答
            List<String> answerList = new ArrayList<>();
            {
                String sql = "select answer from results where acid = ?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, acid);

                    ClientPreparedStatement cps = (ClientPreparedStatement) ps;
                    PreparedQuery pq = (PreparedQuery) cps.getQuery();
                    System.out.println(pq.asSql());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String answer = rs.getString("answer");
                            answerList.add(answer);
                        }
                    }
                }
            }

            // 将回答转换成每个人选择了什么
            for (String answer : answerList) {
                Map<String, String> quidToAnswer = objectMapper.readValue(answer, new TypeReference<Map<String, String>>() {
                });
                for (Map.Entry<String, String> entry : quidToAnswer.entrySet()) {
                    String quidString = entry.getKey();
                    int quid = Integer.parseInt(quidString);
                    AnswerCount answerCount = quidToAnswerCountsMap.get(quid);
                    if (answerCount == null) {
                        throw new RuntimeException("回答中出现了一个未绑定的题号");
                    }
                    String a = entry.getValue();
                    switch (a) {
                        case "A":
                            answerCount.countOfA++;
                            break;
                        case "B":
                            answerCount.countOfB++;
                            break;
                        case "C":
                            answerCount.countOfC++;
                            break;
                        case "D":
                            answerCount.countOfD++;
                            break;
                        default:
                            throw new RuntimeException("回答中出现了 A、B、C、D 之外的选项");
                    }
                }
            }

            List<Result> resultList = new ArrayList<>();
            for (Integer quid : quidList) {
                Question question = quidToQuestionMap.get(quid);
                AnswerCount answerCount = quidToAnswerCountsMap.get(quid);
                Result result = new Result(question, answerCount);
                resultList.add(result);
            }

            return resultList;
        }
    }
}
