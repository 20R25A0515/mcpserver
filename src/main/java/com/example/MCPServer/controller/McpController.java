package com.example.MCPServer.controller;

import com.example.MCPServer.core.ToolDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final ToolDispatcher dispatcher;
    private final ObjectMapper mapper = new ObjectMapper();

    public McpController(ToolDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    // --- GET request for Copilot testing ---
    @GetMapping
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "MCP Server is running",
                "tools", new String[]{"getEmployeeDetails", "getEmployeeLeave"}
        ));
    }

    // --- POST request to execute MCP tools ---
    @PostMapping
    public ResponseEntity<?> handleMcp(@RequestBody(required = false) Map<String, Object> body) {

        // Copilot sends empty POST for testing
        if (body == null || body.get("tool") == null) {
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "MCP Server is running"
            ));
        }

        String tool = (String) body.get("tool");
        Object args = body.get("arguments");

        JsonNode arguments = mapper.convertValue(args, JsonNode.class);

        try {
            Object result = dispatcher.invoke(tool, arguments);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
