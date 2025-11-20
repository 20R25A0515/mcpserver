package com.example.MCPServer.Service;


import com.example.MCPServer.Service.EmployeeService;
import com.example.MCPServer.Service.dto.Employee;
import com.example.MCPServer.dao.EmployeeDao;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    public Map<String, Object> getLeaveSummary(String email) {

        Employee e = dao.findByEmail(email);

        if (e == null) {
            return null;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("email", e.getEmail());
        map.put("name", e.getName());
        map.put("sickLeaves", e.getSickLeaves());
        map.put("casualLeaves", e.getCasualLeaves());
        map.put("earnedLeaves", e.getEarnedLeaves());

        return map;
    }


}
