package com.scott.schedule.model;

/**
 * 教室类型枚举
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum ClassroomType {
    /**
     * 普通教室
     */
    NORMAL("普通教室"),

    /**
     * 实验室
     */
    LAB("实验室"),

    /**
     * 多媒体教室
     */
    MULTIMEDIA("多媒体教室"),

    /**
     * 阶梯教室
     */
    AMPHITHEATER("阶梯教室"),

    /**
     * 体育馆
     */
    GYM("体育馆"),

    /**
     * 机房
     */
    COMPUTER_LAB("机房"),

    /**
     * 语音室
     */
    LANGUAGE_LAB("语音室"),

    /**
     * 会议室
     */
    CONFERENCE_ROOM("会议室");

    private final String description;

    ClassroomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

