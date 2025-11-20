package com.example.MCPServer.websocket;

import com.example.MCPServer.controller.McpRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class McpWebSocketHandler extends TextWebSocketHandler {
    private final McpRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper();

    public McpWebSocketHandler(McpRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode node = mapper.readTree(message.getPayload());
        String type = node.get("type").asText();
        String id = node.get("id").asText();
        String method = node.has("method") ? node.get("method").asText() : null;

        Map<String, Object> result = new HashMap<>();
        if ("request".equals(type)) {
            switch (method) {
                case "list_tools":
                    result.put("tools", registry.getToolDefinitions());
                    break;
                case "list_resources":
                    result.put("resources", registry.getResources());
                    break;
                case "call_tool":
                    String toolName = node.get("params").get("name").asText();
                    JsonNode args = node.get("params").get("arguments");
                    Object res = registry.invoke(toolName, args, Collections.emptyMap());
                    result.put("result", res);
                    break;
                default:
                    result.put("error", "Unknown method: " + method);
            }
        }
        Map<String, Object> response = Map.of("type", "response", "id", id, "result", result);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
    }
}