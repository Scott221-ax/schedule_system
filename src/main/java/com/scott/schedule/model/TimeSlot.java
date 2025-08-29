package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间段数据模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {

    /**
     * 时间段ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 星期几
     */
    @JsonProperty("dayOfWeek")
    private String dayOfWeek;

    /**
     * 节次 (1-5)
     */
    @JsonProperty("period")
    private int period;

    /**
     * 时间范围 (如: 08:00-09:40)
     */
    @JsonProperty("timeRange")
    private String timeRange;

    /**
     * 星期几的数字表示 (1-7, 1为周一)
     */
    @JsonProperty("dayNumber")
    private int dayNumber;

    /**
     * 开始时间 (分钟数，从00:00开始计算)
     */
    @JsonProperty("startMinutes")
    private int startMinutes;

    /**
     * 结束时间 (分钟数，从00:00开始计算)
     */
    @JsonProperty("endMinutes")
    private int endMinutes;

    /**
     * 是否为晚上时间段
     */
    @JsonProperty("evening")
    private boolean evening = false;

    /**
     * 是否可用
     */
    @JsonProperty("available")
    private boolean available = true;

    /**
     * 备注
     */
    @JsonProperty("notes")
    private String notes;
}

