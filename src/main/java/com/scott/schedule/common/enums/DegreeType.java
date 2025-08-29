package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 学位类型枚举
 *
 * @author meituan
 * @since 2024-01-01
 */
@Getter
public enum DegreeType {

    BACHELOR("BACHELOR", "本科"),
    MASTER("MASTER", "硕士"),
    DOCTOR("DOCTOR", "博士");

    private final String code;
    private final String description;

    DegreeType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

