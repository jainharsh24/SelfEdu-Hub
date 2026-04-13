package com.harsh.mini_project.dto;

public class ProfileChartPoint {
    private final String label;
    private final double value;

    public ProfileChartPoint(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public double getValue() {
        return value;
    }
}
