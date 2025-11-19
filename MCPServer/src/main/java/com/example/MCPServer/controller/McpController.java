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

    @PostMapping
    public ResponseEntity<?> handleMcp(@RequestBody Map<String, Object> body) {

        String tool = (String) body.get("tool");
        Object args = body.get("arguments");

        if (tool == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tool name is missing"));
        }

        JsonNode arguments = mapper.convertValue(args, JsonNode.class);

        try {
            Object result = dispatcher.invoke(tool, arguments);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
