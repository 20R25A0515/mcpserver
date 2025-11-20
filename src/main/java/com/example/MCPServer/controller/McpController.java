//package com.example.MCPServer.controller;
//
//import com.example.MCPServer.core.ToolDispatcher;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/mcp")
//public class McpController {
//
//    private final ToolDispatcher dispatcher;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    public McpController(ToolDispatcher dispatcher) {
//        this.dispatcher = dispatcher;
//    }
//
//    // --- GET request for Copilot testing ---
//    @GetMapping
//    public ResponseEntity<?> healthCheck() {
//        return ResponseEntity.ok(Map.of(
//                "status", "ok",
//                "message", "MCP Server is running",
//                "tools", new String[]{"getEmployeeDetails", "getEmployeeLeave"}
//        ));
//    }
//
//    // --- POST request to execute MCP tools ---
//    @PostMapping
//    public ResponseEntity<?> handleMcp(@RequestBody(required = false) Map<String, Object> body) {
//
//        // Copilot sends empty POST for testing
//        if (body == null || body.get("tool") == null) {
//            return ResponseEntity.ok(Map.of(
//                    "status", "ok",
//                    "message", "MCP Server is running"
//            ));
//        }
//
//        String tool = (String) body.get("tool");
//        Object args = body.get("arguments");
//
//        JsonNode arguments = mapper.convertValue(args, JsonNode.class);
//
//        try {
//            Object result = dispatcher.invoke(tool, arguments);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
//        }
//    }
//}


package com.example.MCPServer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * MCP protocol endpoints:
 *  - GET  /mcp              (required by Copilot Studio)
 *  - GET  /mcp/status
 *  - GET  /mcp/tools
 *  - GET  /mcp/resources
 *  - POST /mcp/execute
 */
@RestController
@RequestMapping("/mcp")
public class McpController {

    private final McpRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper();

    public McpController(McpRegistry registry) {
        this.registry = registry;
    }

    /**
     * ✅ REQUIRED FOR COPILOT STUDIO
     * Copilot calls GET /mcp first to test the server.
     * Without this, you'll get 404 Not Found.
     */
    @GetMapping
    public ResponseEntity<?> root() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "MCP server root endpoint",
                "endpoints", List.of("/mcp/status", "/mcp/tools", "/mcp/resources", "/mcp/execute")
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "MCP Server running"));
    }

    /**
     * Return list of tools (name, description, inputSchema)
     * Copilot Studio will call this to list available tools.
     */
    @GetMapping("/tools")
    public ResponseEntity<?> tools() {
        return ResponseEntity.ok(Map.of("tools", registry.getToolDefinitions()));
    }

    /**
     * Return resources (optional informative context).
     */
    @GetMapping("/resources")
    public ResponseEntity<?> resources() {
        return ResponseEntity.ok(Map.of("resources", registry.getResources()));
    }

    /**
     * Execute a tool.
     */
    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody Map<String, Object> body,
                                     @RequestHeader Map<String, String> headers) {
        if (body == null || body.get("tool") == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tool name is missing"));
        }

        String tool = (String) body.get("tool");
        Object argsObj = body.get("arguments");
        JsonNode arguments = mapper.convertValue(argsObj, JsonNode.class);

        try {
            Object result = registry.invoke(tool, arguments, headers);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(400).body(Map.of("error", iae.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error", "details", e.getMessage()));
        }
    }
}
