package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 教师数据模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    /**
     * 教师ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 教师姓名
     */
    @JsonProperty("name")
    private String name;

    /**
     * 所属院系
     */
    @JsonProperty("department")
    private String department;

    /**
     * 职称
     */
    @JsonProperty("title")
    private String title;

    /**
     * 邮箱
     */
    @JsonProperty("email")
    private String email;

    /**
     * 电话
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * 最大周课时数
     */
    @JsonProperty("maxHoursPerWeek")
    private int maxHoursPerWeek = 16;

    /**
     * 偏好时间段ID列表
     */
    @JsonProperty("preferredTimeSlots")
    private List<String> preferredTimeSlots;

    /**
     * 不可用时间段ID列表
     */
    @JsonProperty("unavailableTimeSlots")
    private List<String> unavailableTimeSlots;

    /**
     * 专业领域
     */
    @JsonProperty("specializations")
    private List<String> specializations;

    /**
     * 是否为兼职教师
     */
    @JsonProperty("partTime")
    private boolean partTime = false;
}

