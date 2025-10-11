package com.khpi.wanderua.enums;

public enum GoWithOptions {
    SOLO("Один/одна"),
    COUPLE("Пара"),
    FAMILY("Сім'я"),
    FRIENDS("Друзі"),
    BUSINESS("Бізнес"),
    OTHER("Інше");

    private final String displayName;

    GoWithOptions(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
