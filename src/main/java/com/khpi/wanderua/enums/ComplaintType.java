package com.khpi.wanderua.enums;

public enum ComplaintType {
    SPAM("Спам"),
    INAPPROPRIATE_CONTENT("Неприйнятний контент"),
    FRAUD("Шахрайство"),
    OTHER("Інше");

    private final String displayName;

    ComplaintType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
