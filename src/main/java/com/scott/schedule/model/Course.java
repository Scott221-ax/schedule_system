package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 课程数据模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    /**
     * 课程ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 课程名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 学分
     */
    @JsonProperty("credits")
    private int credits;

    /**
     * 每周课时数
     */
    @JsonProperty("hoursPerWeek")
    private int hoursPerWeek;

    /**
     * 授课教师ID
     */
    @JsonProperty("teacherId")
    private String teacherId;

    /**
     * 学生人数
     */
    @JsonProperty("studentCount")
    private int studentCount;

    /**
     * 课程类型
     */
    @JsonProperty("courseType")
    private CourseType courseType;

    /**
     * 所属班级列表
     */
    @JsonProperty("classIds")
    private List<String> classIds;

    /**
     * 课程描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 是否为必修课
     */
    @JsonProperty("required")
    private boolean required = true;

    /**
     * 课程难度等级 (1-5)
     */
    @JsonProperty("difficultyLevel")
    private int difficultyLevel = 3;

    /**
     * 先修课程ID列表
     */
    @JsonProperty("prerequisiteIds")
    private List<String> prerequisiteIds;

    /**
     * 课程度数（约束数量）
     */
    @JsonProperty("degree")
    private double degree = 0.0;
}

