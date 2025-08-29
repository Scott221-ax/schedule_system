package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 班级类型枚举
 *
 * @author meituan
 * @since 2024-01-01
 */
@Getter
public enum ClassType {

    REGULAR("REGULAR", "普通班"),
    KEY("KEY", "重点班"),
    EXPERIMENTAL("EXPERIMENTAL", "实验班"),
    ART("ART", "艺术班"),
    SPORTS("SPORTS", "体育班");

    private final String code;
    private final String description;

    ClassType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

