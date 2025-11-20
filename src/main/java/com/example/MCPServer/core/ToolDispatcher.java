package com.example.MCPServer.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ToolDispatcher {

    private final Map<String, ToolHandler> tools = new HashMap<>();
    private final List<ToolHandler> registeredTools;

    public ToolDispatcher(List<ToolHandler> registeredTools) {
        this.registeredTools = registeredTools;
    }

    @PostConstruct
    public void initTools() {
        for (ToolHandler t : registeredTools) {
            tools.put(t.name(), t);
            System.out.println("MCP Tool Registered: " + t.name());
        }
    }

    public Object invoke(String toolName, JsonNode arguments) throws Exception {
        ToolHandler handler = tools.get(toolName);

        if (handler == null) {
            throw new IllegalArgumentException("Tool not found: " + toolName);
        }

        return handler.handle(arguments);
    }
}
