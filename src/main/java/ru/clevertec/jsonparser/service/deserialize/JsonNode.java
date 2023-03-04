package ru.clevertec.jsonparser.service.deserialize;

public class JsonNode {
    private String name;
    private NodeType type;

    public JsonNode(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }
}
