package com.javaforge.expense.model;

public enum Category {
    FOOD("Food & Dining"),
    TRANSPORT("Transportation"),
    ENTERTAINMENT("Entertainment"),
    UTILITIES("Utilities & Bills"),
    SHOPPING("Shopping"),
    HEALTH("Health & Medical"),
    EDUCATION("Education"),
    RENT("Rent & Housing"),
    SALARY("Salary & Income"),
    FREELANCE("Freelance Income"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
