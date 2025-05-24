package com.was;

public enum ResponseHeaderAttribute {
    DATE("Date"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type");

    private final String attribute;

    ResponseHeaderAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}
