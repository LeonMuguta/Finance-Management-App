package com.finance.financial_management_app.budget;

public enum Month {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    // Convert integer month to Enum (1-12)
    public static Month fromInt(int month) {
        return Month.values()[month - 1];
    }
}
