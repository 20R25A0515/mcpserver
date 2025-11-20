package com.example.MCPServer.config;

import com.example.MCPServer.websocket.McpWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class McpWebSocketConfig implements WebSocketConfigurer {
    private final McpWebSocketHandler handler;

    public McpWebSocketConfig(McpWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/mcp/ws").setAllowedOrigins("*");
    }
}

