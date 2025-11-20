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
 *  - GET  /mcp
 *  - POST /mcp     <-- Copilot Studio needs this for handshake
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
     * 🔵 Copilot Studio calls this first (GET handshake)
     */
    @GetMapping
    public ResponseEntity<?> rootGet() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "MCP server root endpoint (GET)",
                "endpoints", List.of("/mcp/status", "/mcp/tools", "/mcp/resources", "/mcp/execute")
        ));
    }

    /**
     * 🔵 Copilot Studio ALSO calls POST /mcp (POST handshake)
     */
    @PostMapping
    public ResponseEntity<?> rootPost() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "MCP server root endpoint (POST)",
                "endpoints", List.of("/mcp/status", "/mcp/tools", "/mcp/resources", "/mcp/execute")
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "MCP Server running"));
    }

    @GetMapping("/tools")
    public ResponseEntity<?> tools() {
        return ResponseEntity.ok(Map.of("tools", registry.getToolDefinitions()));
    }

    @GetMapping("/resources")
    public ResponseEntity<?> resources() {
        return ResponseEntity.ok(Map.of("resources", registry.getResources()));
    }

    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody Map<String, Object> body,
                                     @RequestHeader Map<String, String> headers) {

        if (body == null || body.get("tool") == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tool name is missing"));
        }

        String tool = (String) body.get("tool");
        JsonNode arguments = mapper.convertValue(body.get("arguments"), JsonNode.class);

        try {
            Object result = registry.invoke(tool, arguments, headers);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(Map.of("error", iae.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error", "details", e.getMessage()));
        }
    }
}

