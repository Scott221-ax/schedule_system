package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 学科类型枚举
 *
 * @author mazhenpeng
 * @since 2025/8/29
 */
@Getter
public enum SubjectType {

    MAIN("MAIN", "主科"),
    MINOR("MINOR", "副科"),
    ELECTIVE("ELECTIVE", "选修课");

    private final String code;
    private final String description;

    SubjectType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

