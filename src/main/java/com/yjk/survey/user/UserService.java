package com.yjk.survey.user;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
@Service
public class UserService {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User register(String username, String password) {
        String salt = BCrypt.gensalt();
        String passwordHash = BCrypt.hashpw(password, salt);

        String sql = "insert into users (username, password) values (:username, :password)";
        SqlParameterSource parameterSource = new MapSqlParameterSource(new HashMap<String, Object>() {{
            put("username", username);
            put("password", passwordHash);
        }});
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parameterSource, keyHolder);

        User user = new User();
        user.uid = keyHolder.getKeyAs(BigInteger.class).intValue();
        user.username = username;

        return user;
    }

    public User login(String username, String password) {
        String sql = "select uid, password from users where username = :username";
        SqlParameterSource parameterSource = new MapSqlParameterSource(new HashMap<String, Object>() {{
            put("username", username);
        }});
        log.debug("执行 SQL: {}", parameterSource);
        return jdbcTemplate.query(sql, parameterSource, new ResultSetExtractor<User>() {
            @Override
            public User extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (!rs.next()) {
                    log.debug("没有查到任何用户");
                    return null;
                }

                int uid = rs.getInt("uid");
                String passwordHash = rs.getString("password");
                if (!BCrypt.checkpw(password, passwordHash)) {
                    log.debug("密码错误");
                    return null;
                }

                User user = new User();
                user.uid = uid;
                user.username = username;

                return user;
            }
        });
    }
}
