package com.ocP6.PayMyBuddy.cleanup;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanup {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PreDestroy
    public void cleanup() {
        jdbcTemplate.execute("DROP DATABASE IF EXISTS paymybuddy_test");
    }
}