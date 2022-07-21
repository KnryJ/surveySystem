package com.yjk.survey.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public QuestionService(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public void save(int uid, String question, String optionA, String optionB, String optionC, String optionD) {
        String[] optionArray = new String[] { optionA, optionB, optionC, optionD };
        String options = objectMapper.writeValueAsString(optionArray);
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("uid", uid);
        parameterMap.put("question", question);
        parameterMap.put("options", options);

        String sql = "insert into questions (uid, question, options) values (:uid, :question, :options)";
        jdbcTemplate.update(sql, parameterMap);
    }

    @SneakyThrows
    public List<Object> list(int uid) {
        String sql = "select quid, question, options from questions where uid = :uid order by quid";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("uid", uid);
        List<Object> list = new ArrayList<>();
        jdbcTemplate.query(sql, parameterMap, new RowCallbackHandler() {
            @Override
            @SneakyThrows
            public void processRow(ResultSet rs) throws SQLException {
                HashMap<String, Object> q = new HashMap<>();
                q.put("quid", rs.getInt("quid"));
                q.put("题目", rs.getString("question"));
                String options = rs.getString("options");
                List<?> optionList = objectMapper.readValue(options, List.class);
                q.put("选项", optionList);
                list.add(q);
            }
        });
        return list;
    }
}
