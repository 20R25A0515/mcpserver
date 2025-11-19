package com.example.MCPServer.Service;


import com.example.MCPServer.Service.EmployeeService;
import com.example.MCPServer.Service.dto.Employee;
import com.example.MCPServer.dao.EmployeeDao;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmployeeService {
    private final EmployeeDao dao;
    public EmployeeService(EmployeeDao dao) {
        this.dao = dao;
    }

    public Employee getEmployee(String email) {
        return dao.findByEmail(email);
    }

    // Example of combining logic: compute leave summary
    public Object getLeaveSummary(String email) {
        Employee e = dao.findByEmail(email);
        if (e == null) return null;
        return Map.of(
                "email", e.getEmail(),
                "name", e.getName(),
                "sickLeaves", e.getSickLeaves(),
                "casualLeaves", e.getCasualLeaves(),
                "earnedLeaves", e.getEarnedLeaves()
        );
    }


}
