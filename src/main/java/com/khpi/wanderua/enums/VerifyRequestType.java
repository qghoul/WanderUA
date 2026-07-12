package com.khpi.wanderua.enums;

public enum VerifyRequestType {
    BUSINESS_VERIFY("business verification"),
    SUSTAINABLE_VERIFY("sustainability verification");

    private final String description;

    VerifyRequestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
