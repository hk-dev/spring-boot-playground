package com.hkdev.enums;

public enum Plans {

    BASIC(1, "Basic"),
    PRO(2, "Pro");

    private int id;
    private String planName;

    Plans(int id, String planName) {
        this.id = id;
        this.planName = planName;
    }

    public int getId() {
        return id;
    }

    public String getPlanName() {
        return planName;
    }
}
