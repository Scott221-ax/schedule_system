package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 学校类型枚举
 *
 * @author meituan
 * @since 2024-01-01
 */
@Getter
public enum SchoolType {

    MIDDLE("MIDDLE", "初中"),
    HIGH("HIGH", "高中"),
    COMBINED("COMBINED", "联合中学");

    private final String code;
    private final String description;

    SchoolType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

