package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学生数据模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    /**
     * 学生ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 学生姓名
     */
    @JsonProperty("name")
    private String name;

    /**
     * 班级
     */
    @JsonProperty("className")
    private String className;

    /**
     * 年级
     */
    @JsonProperty("grade")
    private String grade;

    /**
     * 邮箱
     */
    @JsonProperty("email")
    private String email;

    /**
     * 专业
     */
    @JsonProperty("major")
    private String major;

    /**
     * 学号
     */
    @JsonProperty("studentNumber")
    private String studentNumber;

    /**
     * 选修课程ID列表
     */
    @JsonProperty("electiveCourseIds")
    private List<String> electiveCourseIds;

    /**
     * 不可用时间段ID列表
     */
    @JsonProperty("unavailableTimeSlots")
    private List<String> unavailableTimeSlots;
}

