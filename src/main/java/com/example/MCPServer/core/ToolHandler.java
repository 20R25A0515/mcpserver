package com.example.MCPServer.core;

import com.fasterxml.jackson.databind.JsonNode;

public interface ToolHandler {
    String name();
    Object handle(JsonNode arguments) throws Exception;
}
