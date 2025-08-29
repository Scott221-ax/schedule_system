package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 排课结果模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResult {

    /**
     * 结果ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 算法名称
     */
    @JsonProperty("algorithmName")
    private String algorithmName;

    /**
     * 生成时间
     */
    @JsonProperty("generatedTime")
    private LocalDateTime generatedTime;

    /**
     * 适应度分数
     */
    @JsonProperty("fitnessScore")
    private double fitnessScore;

    /**
     * 执行时间（毫秒）
     */
    @JsonProperty("executionTimeMs")
    private long executionTimeMs;

    /**
     * 课程安排列表
     */
    @JsonProperty("courseAssignments")
    private List<CourseAssignment> courseAssignments;

    /**
     * 约束违反统计
     */
    @JsonProperty("constraintViolations")
    private Map<String, Integer> constraintViolations;

    /**
     * 算法参数
     */
    @JsonProperty("algorithmParameters")
    private Map<String, Object> algorithmParameters;

    /**
     * 是否为可行解
     */
    @JsonProperty("feasible")
    private boolean feasible;

    /**
     * 备注
     */
    @JsonProperty("notes")
    private String notes;

    /**
     * 课程安排内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseAssignment {
        @JsonProperty("courseId")
        private String courseId;

        @JsonProperty("courseName")
        private String courseName;

        @JsonProperty("teacherId")
        private String teacherId;

        @JsonProperty("teacherName")
        private String teacherName;

        @JsonProperty("classroomId")
        private String classroomId;

        @JsonProperty("classroomName")
        private String classroomName;

        @JsonProperty("timeSlotId")
        private String timeSlotId;

        @JsonProperty("dayOfWeek")
        private String dayOfWeek;

        @JsonProperty("period")
        private int period;

        @JsonProperty("timeRange")
        private String timeRange;

        @JsonProperty("studentCount")
        private int studentCount;
    }
}

