package com.example.MCPServer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.MCPServer.Service.EmployeeService;
import com.example.MCPServer.Service.dto.Employee;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Registry contains tool definitions and provides invocation.
 * Extendable: add more tools here.
 */
@Component
public class McpRegistry {

    private final EmployeeService employeeService;

    private final Map<String, ToolDef> tools = new LinkedHashMap<>();
    private final List<Map<String,Object>> resources = new ArrayList<>();

    public McpRegistry(EmployeeService employeeService) {
        this.employeeService = employeeService;
        registerBuiltInTools();
        registerResources();
    }

    private void registerBuiltInTools() {
        // Tool: getEmployeeDetails
        ToolDef details = new ToolDef(
                "getEmployeeDetails",
                "Fetch employee details by email",
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "email", Map.of("type", "string", "description", "Employee email")
                        ),
                        "required", List.of("email")
                )
        );
        tools.put(details.getName(), details);

        // Tool: getEmployeeLeave
        ToolDef leave = new ToolDef(
                "getEmployeeLeave",
                "Get leave summary by email",
                Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "email", Map.of("type", "string", "description", "Employee email")
                        ),
                        "required", List.of("email")
                )
        );
        tools.put(leave.getName(), leave);
    }

    private void registerResources() {
        // Example resource: file uploaded by user (use the local path you uploaded)
        // We include it as a file URL; render/hosting may require you to host it in public storage.
        // Per developer instruction, using the uploaded file path as URL:
        String uploadedFilePath = "/mnt/data/70700fb7-6d30-4386-a9f6-7dd144ba0ead.png";

        Map<String,Object> res = new HashMap<>();
        res.put("name", "hr-sample-image");
        res.put("description", "Example resource (uploaded screenshot)");
        res.put("mimeType", "image/png");
        res.put("url", uploadedFilePath); // NOTE: Copilot Studio may require an https URL; you can replace with hosted URL.
        resources.add(res);
    }

    // Return definitions in Copilot-friendly JSON
    public List<Map<String,Object>> getToolDefinitions() {
        List<Map<String,Object>> out = new ArrayList<>();
        for (ToolDef t : tools.values()) out.add(t.toMap());
        return out;
    }

    public List<Map<String,Object>> getResources() {
        return resources;
    }

    public Object invoke(String toolName, JsonNode arguments, Map<String,String> headers) throws Exception {
        ToolDef tool = tools.get(toolName);
        if (tool == null) throw new IllegalArgumentException("Tool not found: " + toolName);

        // dispatch by name - extendable
        switch (toolName) {
            case "getEmployeeDetails":
                return invokeGetEmployeeDetails(arguments);
            case "getEmployeeLeave":
                return invokeGetEmployeeLeave(arguments);
            default:
                throw new IllegalArgumentException("Unhandled tool: " + toolName);
        }
    }

    private Map<String,Object> invokeGetEmployeeDetails(JsonNode args) {
        String email = args.has("email") ? args.get("email").asText() : null;
        if (email == null) throw new IllegalArgumentException("email is required");
        Employee e = employeeService.getEmployee(email);
        if (e == null) return Map.of("found", false);
        // map to serializable
        return Map.of(
                "found", true,
                "id", e.getId(),
                "name", e.getName(),
                "email", e.getEmail(),
                "position", e.getPosition(),
                "sickLeaves", e.getSickLeaves(),
                "casualLeaves", e.getCasualLeaves(),
                "earnedLeaves", e.getEarnedLeaves()
        );
    }

    private Map<String,Object> invokeGetEmployeeLeave(JsonNode args) {

        String email = args.has("email") ? args.get("email").asText() : null;
        if (email == null) throw new IllegalArgumentException("email is required");

        Map<String,Object> summary = employeeService.getLeaveSummary(email);

        if (summary == null) {
            return Map.of("found", false);
        }

        // convert to mutable map
        Map<String,Object> result = new HashMap<>(summary);
        result.put("found", true);

        return result;
    }

    // Simple tool definition holder
    public static class ToolDef {
        private final String name;
        private final String description;
        private final Object inputSchema;

        public ToolDef(String name, String description, Object inputSchema) {
            this.name = name; this.description = description; this.inputSchema = inputSchema;
        }
        public String getName(){return name;}
        public Map<String,Object> toMap(){
            return Map.of(
                    "name", name,
                    "description", description,
                    "inputSchema", inputSchema
            );
        }
    }
}

