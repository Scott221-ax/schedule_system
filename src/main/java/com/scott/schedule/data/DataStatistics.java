package com.scott.schedule.data;

import lombok.Data;

/**
 * 数据统计类
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
public class DataStatistics {

    /**
     * 课程数量
     */
    private int courseCount;

    /**
     * 教师数量
     */
    private int teacherCount;

    /**
     * 教室数量
     */
    private int classroomCount;

    /**
     * 时间段数量
     */
    private int timeSlotCount;

    /**
     * 学生数量
     */
    private int studentCount;

    /**
     * 排课结果数量
     */
    private int scheduleResultCount;

    /**
     * 获取总数据量
     *
     * @return 总数据量
     */
    public int getTotalCount() {
        return courseCount + teacherCount + classroomCount + timeSlotCount + studentCount;
    }

    /**
     * 计算平均每个教师的课程数
     *
     * @return 平均课程数
     */
    public double getAverageCoursesPerTeacher() {
        return teacherCount > 0 ? (double) courseCount / teacherCount : 0.0;
    }

    /**
     * 计算平均每个时间段的教室数
     *
     * @return 平均教室数
     */
    public double getAverageClassroomsPerTimeSlot() {
        return timeSlotCount > 0 ? (double) classroomCount / timeSlotCount : 0.0;
    }

    /**
     * 获取统计摘要
     *
     * @return 统计摘要
     */
    public String getSummary() {
        return String.format(
                """
                数据统计摘要:
                - 课程数量: %d
                - 教师数量: %d (平均每人 %.1f 门课)
                - 教室数量: %d
                - 时间段数量: %d (平均每时段 %.1f 个教室)
                - 学生数量: %d
                - 排课结果: %d
                - 总数据量: %d
                """,
                courseCount, teacherCount, getAverageCoursesPerTeacher(),
                classroomCount, timeSlotCount, getAverageClassroomsPerTimeSlot(),
                studentCount, scheduleResultCount, getTotalCount()
        );
    }
}

