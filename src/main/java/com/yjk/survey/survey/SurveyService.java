package com.yjk.survey.survey;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SurveyService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public SurveyService(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public void save(int uid, String title, String brief) {
        String sql = "insert into surveys (uid, title, brief) values (:uid, :title, :brief)";
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("uid", uid);
        parameterMap.put("title", title);
        parameterMap.put("brief", brief);

        jdbcTemplate.update(sql, parameterMap);
    }

    @SneakyThrows
    public List<Object> list(int uid) {
        String sql = "select suid, title, brief from surveys where uid = :uid order by suid";
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("uid", uid);
        List<Object> list = new ArrayList<>();
        jdbcTemplate.query(sql, parameterMap, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                HashMap<String, Object> s = new HashMap<>();
                s.put("suid", rs.getInt("suid"));
                s.put("标题", rs.getString("title"));
                s.put("简介", rs.getString("brief"));

                list.add(s);
            }
        });

        return list;
    }

    public Map<String, Object> map(int uid, int suid) {
        HashMap<String, Object> map = new HashMap<>();
        {
            String sql = "select title, brief from surveys where suid = :suid and uid = :uid";
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("suid", suid);
            parameterMap.put("uid", uid);
            jdbcTemplate.query(sql, parameterMap, new ResultSetExtractor<Void>() {
                @Override
                public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
                    rs.next();
                    map.put("suid", suid);
                    map.put("title", rs.getString("title"));
                    map.put("brief", rs.getString("brief"));
                    return null;
                }
            });
        }

        {
            String sql = "select q.quid, question, ifnull(t.quid, 0) bounded from questions q\n" +
                    "left join\n" +
                    "(select quid from survey_question_relations where suid = :suid) t\n" +
                    "on q.quid = t.quid where uid = :uid";

            List<Object> list = new ArrayList<>();
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("suid", suid);
            parameterMap.put("uid", uid);
            jdbcTemplate.query(sql, parameterMap, new ResultSetExtractor<Void>() {
                @Override
                public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
                    while (rs.next()) {
                        HashMap<String, Object> q = new HashMap<>();
                        q.put("quid", rs.getInt("quid"));
                        q.put("question", rs.getString("question"));
                        q.put("isBounden", rs.getBoolean("bounded"));

                        list.add(q);
                    }
                    return null;
                }
            });
            map.put("questionList", list);
        }

        return map;
    }

    @SneakyThrows
    public void bind(int suid, int[] bindQuid) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement("delete from survey_question_relations where suid = ?")) {
                ps.setInt(1, suid);
                ps.executeUpdate();
            }

            for (int quid : bindQuid) {
                try (PreparedStatement ps = c.prepareStatement("insert into survey_question_relations (suid, quid) values (?, ?)")) {
                    ps.setInt(1, suid);
                    ps.setInt(2, quid);
                    ps.executeUpdate();
                }
            }

            c.commit();
        }
    }
}
