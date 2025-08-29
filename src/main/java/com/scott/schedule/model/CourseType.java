package com.scott.schedule.model;

/**
 * 课程类型枚举
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum CourseType {
    /**
     * 理论课
     */
    THEORY("理论课"),

    /**
     * 实验课
     */
    LAB("实验课"),

    /**
     * 实践课
     */
    PRACTICE("实践课"),

    /**
     * 讨论课
     */
    SEMINAR("讨论课"),

    /**
     * 体育课
     */
    PE("体育课"),

    /**
     * 艺术课
     */
    ART("艺术课");

    private final String description;

    CourseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

