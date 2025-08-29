package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 课程类型枚举
 *
 * @author meituan
 * @since 2024-01-01
 */
@Getter
public enum CourseType {

    REQUIRED("REQUIRED", "必修课"),
    ELECTIVE("ELECTIVE", "选修课"),
    PUBLIC("PUBLIC", "公共课");

    private final String code;
    private final String description;

    CourseType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

