package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 教室数据模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    /**
     * 教室ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 教室名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 容量
     */
    @JsonProperty("capacity")
    private int capacity;

    /**
     * 教室类型
     */
    @JsonProperty("type")
    private ClassroomType type;

    /**
     * 所在建筑
     */
    @JsonProperty("building")
    private String building;

    /**
     * 楼层
     */
    @JsonProperty("floor")
    private int floor;

    /**
     * 房间号
     */
    @JsonProperty("roomNumber")
    private String roomNumber;

    /**
     * 设备列表
     */
    @JsonProperty("equipment")
    private List<String> equipment;

    /**
     * 是否可用
     */
    @JsonProperty("available")
    private boolean available = true;

    /**
     * 维护时间段ID列表
     */
    @JsonProperty("maintenanceTimeSlots")
    private List<String> maintenanceTimeSlots;

    /**
     * 备注
     */
    @JsonProperty("notes")
    private String notes;
}

