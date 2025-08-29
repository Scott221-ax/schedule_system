package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 教室类型枚举
 *
 * @author meituan
 * @since 2024-01-01
 */
@Getter
public enum RoomType {

    NORMAL("NORMAL", "普通教室"),
    LAB("LAB", "实验室"),
    MULTIMEDIA("MULTIMEDIA", "多媒体教室"),
    AMPHITHEATER("AMPHITHEATER", "阶梯教室");

    private final String code;
    private final String description;

    RoomType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

