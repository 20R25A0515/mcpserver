package com.example.MCPServer.dao;


import com.example.MCPServer.Service.dto.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeDao {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Employee findByEmail(String email) {
        String sql = """
            SELECT id, name, email, position,
                   sick_leaves, casual_leaves, earned_leaves
            FROM employee
            WHERE email = ?
        """;

        return jdbcTemplate.query(sql, new Object[]{email}, rs -> {
            if (!rs.next()) return null;

            Employee e = new Employee();
            e.setId(rs.getLong("id"));
            e.setName(rs.getString("name"));
            e.setEmail(rs.getString("email"));
            e.setPosition(rs.getString("position"));
            e.setSickLeaves(rs.getInt("sick_leaves"));
            e.setCasualLeaves(rs.getInt("casual_leaves"));
            e.setEarnedLeaves(rs.getInt("earned_leaves"));

            return e;
        });
    }
}
