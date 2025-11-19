package com.example.MCPServer.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.MCPServer.core.ToolHandler;
import com.example.MCPServer.Service.EmployeeService;
import org.springframework.stereotype.Component;

@Component
public class GetEmployeeLeaveTool implements ToolHandler {

    private final EmployeeService service;

    public GetEmployeeLeaveTool(EmployeeService service) {
        this.service = service;
    }

    @Override
    public String name() {
        return "getEmployeeLeave";
    }

    @Override
    public Object handle(JsonNode arguments) {
        String email = arguments.get("email").asText();
        return service.getLeaveSummary(email);
    }
}

